package com.catts.backend.controller;

import com.catts.backend.database.model.AddressEntity;
import com.catts.backend.database.model.AdopterEntity;
import com.catts.backend.database.model.OngEntity;
import com.catts.backend.dto.*;
import com.catts.backend.repository.AdopterRepository;
import com.catts.backend.repository.OngRepository;
import com.catts.backend.service.GoogleAuthService;
import com.catts.backend.service.TokenService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GoogleAuthService googleAuthService;
    private final TokenService tokenService;
    private final AdopterRepository adopterRepository;
    private final OngRepository ongRepository;

    public AuthController(GoogleAuthService googleAuthService,
                          TokenService tokenService,
                          AdopterRepository adopterRepository,
                          OngRepository ongRepository) {
        this.googleAuthService = googleAuthService;
        this.tokenService = tokenService;
        this.adopterRepository = adopterRepository;
        this.ongRepository = ongRepository;
    }

    // =========================================================================
    // 1. LOGIN / VERIFICAÇÃO INICIAL COM GOOGLE
    // =========================================================================
    @PostMapping("/google")
    public ResponseEntity<?> autenticarComGoogle(@RequestBody GoogleLoginRequestDTO dto) {
        GoogleIdToken.Payload payload = googleAuthService.validarTokenGoogle(dto.token());
        String email = payload.getEmail();
        String nome = (String) payload.get("name");

        // 1º: Verifica se já está cadastrado como ADOTANTE
        Optional<AdopterEntity> adotanteOpt = adopterRepository.findByEmail(email);
        if (adotanteOpt.isPresent()) {
            AdopterEntity adotante = adotanteOpt.get();
            String jwt = tokenService.gerarToken(adotante.getId(), adotante.getEmail(), "ADOTANTE");

            return ResponseEntity.ok(new AuthResponseDTO(
                    false, jwt, "ADOTANTE", adotante.getEmail(), adotante.getNome()
            ));
        }

        // 2º: Verifica se já está cadastrado como ONG
        Optional<OngEntity> ongOpt = ongRepository.findByEmail(email);
        if (ongOpt.isPresent()) {
            OngEntity ong = ongOpt.get();
            String jwt = tokenService.gerarToken(ong.getId(), ong.getEmail(), "ONG");

            return ResponseEntity.ok(new AuthResponseDTO(
                    false, jwt, "ONG", ong.getEmail(), ong.getNomeInstitucional()
            ));
        }

        // 3º: Não existe em nenhuma tabela -> devolve os dados do Google para preencher o formulário no Angular
        return ResponseEntity.ok(new AuthResponseDTO(
                true, null, null, email, nome
        ));
    }

    // =========================================================================
    // 2. FINALIZAR CADASTRO COMO ADOTANTE (COM ENDEREÇO VINCULADO)
    // =========================================================================
    @PostMapping("/completar-cadastro/adotante")
    @Transactional // Garante que Adotante e Endereço sejam salvos na mesma transação no banco
    public ResponseEntity<?> completarCadastroAdotante(@RequestBody CompletarCadastroAdotanteDTO dto) {
        GoogleIdToken.Payload payload = googleAuthService.validarTokenGoogle(dto.googleToken());
        String email = payload.getEmail();
        String nome = (String) payload.get("name");

        // Evita que o mesmo e-mail crie duas contas (Adotante + ONG)
        if (adopterRepository.findByEmail(email).isPresent() || ongRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Este e-mail já está cadastrado no sistema."));
        }

        // 1. Monta a entidade de Endereço a partir do objeto aninhado no DTO
        AddressEntity endereco = new AddressEntity();
        if (dto.address() != null) {
            endereco.setStreet(dto.address().street());
            endereco.setNumber(dto.address().number());
            endereco.setCity(dto.address().city());
            endereco.setState(dto.address().state());
            endereco.setCep(dto.address().cep());
        }

        // 2. Monta a entidade de Adotante com os dados do Google + formulário
        AdopterEntity novoAdotante = new AdopterEntity();
        novoAdotante.setNome(nome);
        novoAdotante.setEmail(email);
        novoAdotante.setContato(dto.contato());
        novoAdotante.setCpf(dto.cpf());
        novoAdotante.setDataNascimento(dto.dataNascimento());
        novoAdotante.setTipoResidencia(dto.tipoResidencia());
        novoAdotante.setPossuiTelaProtecao(dto.possuiTelaProtecao());
        novoAdotante.setImovelAlugado(dto.imovelAlugado());
        novoAdotante.setSalario(dto.salario());
        novoAdotante.setQuantidadePet(dto.quantidadePet());
        novoAdotante.setQuantidadeHorasTrabalho(dto.quantidadeHorasTrabalho());
        novoAdotante.setQuantidadePessoasCasa(dto.quantidadePessoasCasa());
        novoAdotante.setDescricao(dto.descricao());
        novoAdotante.setAddress(endereco);

        // 3. Salva o Adotante e o Endereço automaticamente (via CascadeType.ALL)
        AdopterEntity adotanteSalvo = adopterRepository.save(novoAdotante);

        // 4. Gera o JWT definitivo para o usuário já entrar logado
        String jwt = tokenService.gerarToken(adotanteSalvo.getId(), adotanteSalvo.getEmail(), "ADOTANTE");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDTO(false, jwt, "ADOTANTE", adotanteSalvo.getEmail(), adotanteSalvo.getNome()));
    }

    // =========================================================================
    // 3. FINALIZAR CADASTRO COMO ONG
    // =========================================================================
    @PostMapping("/completar-cadastro/ong")
    @Transactional
    public ResponseEntity<?> completarCadastroOng(@RequestBody CompletarCadastroOngDTO dto) {
        GoogleIdToken.Payload payload = googleAuthService.validarTokenGoogle(dto.googleToken());
        String email = payload.getEmail();
        String nome = (String) payload.get("name");

        if (adopterRepository.findByEmail(email).isPresent() || ongRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Este e-mail já está cadastrado no sistema."));
        }

        // 1. Monta a entidade de Endereço a partir do objeto aninhado no DTO
        AddressEntity endereco = new AddressEntity();
        if (dto.address() != null) {
            endereco.setStreet(dto.address().street());
            endereco.setNumber(dto.address().number());
            endereco.setCity(dto.address().city());
            endereco.setState(dto.address().state());
            endereco.setCep(dto.address().cep());
        }

        OngEntity novaOng = new OngEntity();
        novaOng.setNomeInstitucional(nome);
        novaOng.setEmail(email);
        novaOng.setCnpj(dto.CNPJ());
        novaOng.setContato(dto.contato());
        novaOng.setAddress(endereco);
        // Se a sua OngEntity também tiver @OneToOne AddressEntity, basta repetir o mesmo bloco de AddressEntity aqui!

        OngEntity ongSalva = ongRepository.save(novaOng);

        String jwt = tokenService.gerarToken(ongSalva.getId(), ongSalva.getEmail(), "ONG");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDTO(false, jwt, "ONG", ongSalva.getEmail(), ongSalva.getNomeInstitucional()));
    }
}