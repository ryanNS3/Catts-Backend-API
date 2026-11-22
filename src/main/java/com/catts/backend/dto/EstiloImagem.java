package com.catts.backend.dto;

// Estilos de edicao que a ONG pode escolher. O front manda so o NOME (ex.: FUNDO_COLORIDO);
// o texto do prompt fica aqui no back, assim da pra ajustar sem mexer no Angular.
public enum EstiloImagem {

    FUNDO_COLORIDO(
            "Keep the animal exactly the same: same fur, face, eyes, colors, markings, pose and proportions. "
                    + "Replace only the background with a vibrant, solid, saturated color. "
                    + "Soft clean studio lighting, social media style photo. "
                    + "Do not add text, objects or other animals."),

    FUNDO_ANGULO_CORRIGIDO(
            "Keep the animal exactly the same: same fur, face, eyes, colors and markings. "
                    + "Slightly adjust the framing and camera angle so the animal faces the viewer and is centered, "
                    + "without distorting its features. "
                    + "Replace the background with a vibrant, solid, saturated color. "
                    + "Soft clean studio lighting, social media style photo. "
                    + "Do not add text, objects or other animals."),

    FUNDO_DEGRADE(
            "Keep the animal exactly the same: same fur, face, eyes, colors, markings, pose and proportions. "
                    + "Replace only the background with a smooth colorful gradient using two bright complementary colors, "
                    + "trendy social media style. Soft clean studio lighting. "
                    + "Do not add text, objects or other animals.");

    private final String prompt;

    EstiloImagem(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }
}
