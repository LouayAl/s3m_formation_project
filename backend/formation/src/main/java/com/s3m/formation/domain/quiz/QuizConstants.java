package com.s3m.formation.domain.quiz;

import java.util.List;

public class QuizConstants {

    public enum QuestionType { MCQ, VRAI_FAUX }

    public record Choice(String key, String label) {}

    public record Question(
            int id,
            String text,
            QuestionType type,
            List<Choice> choices,
            String correctAnswer,
            String imageUrl
    ) {}

    public static final List<Question> QUESTIONS = List.of(

            new Question(1,
                    "La sécurité au travail est uniquement la responsabilité de la direction.",
                    QuestionType.VRAI_FAUX, List.of(), "FAUX", null),

            new Question(2,
                    "Quelle est la signification de ce panneau ?",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Casque de sécurité peut être porté"),
                            new Choice("b", "Le casque de sécurité doit être porté"),
                            new Choice("c", "Ceci est une zone à risque")
                    ), "b", "/quiz/question-2.png"),

            new Question(3,
                    "Quelle est la signification de ce panneau ?",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Attention, charge suspendue"),
                            new Choice("b", "Danger de mort"),
                            new Choice("c", "Risque de collision")
                    ), "a", "/quiz/question-3.png"),

            new Question(4,
                    "Quelle est la signification de ce panneau ?",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Le gilet haute visibilité est obligatoire"),
                            new Choice("b", "Le gilet de sauvetage est obligatoire"),
                            new Choice("c", "Un gilet de haute visibilité peut être porté")
                    ), "a", "/quiz/question-4.png"),

            new Question(5,
                    "Quelle est la signification de ce panneau ?",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Danger eau profonde"),
                            new Choice("b", "Chute de hauteur"),
                            new Choice("c", "Risque de trébuchement et de glissade")
                    ), "c", "/quiz/question-5.png"),

            new Question(6,
                    "En cas d'urgence, n'utilisez jamais l'ascenseur pour sortir du bâtiment.",
                    QuestionType.VRAI_FAUX, List.of(), "VRAI", null),

            new Question(7,
                    "N'importe qui peut saisir en toute sécurité un extincteur pour éteindre un feu.",
                    QuestionType.VRAI_FAUX, List.of(), "FAUX", null),

            new Question(8,
                    "La manière correcte d'entretenir un extincteur sur le lieu de travail est :",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Retirez la goupille et gardez-la prête à l'emploi"),
                            new Choice("b", "Retirez la goupille, appuyez sur la détente et gardez-le prêt à l'emploi"),
                            new Choice("c", "Conservez-le dans son état d'origine : équipé d'une broche et d'un sceau")
                    ), "c", null),

            new Question(9,
                    "Pour sauver un homme tombé à la mer, la première étape est :",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Gardez un contact visuel avec la victime, lancez l'anneau de sauvetage le plus proche"),
                            new Choice("b", "Plongez dans l'eau pour sauver la victime"),
                            new Choice("c", "Appelez l'autorité portuaire")
                    ), "a", null),

            new Question(10,
                    "L'entretien des « Équipements de Protection Individuelle » est de la responsabilité des opérateurs.",
                    QuestionType.VRAI_FAUX, List.of(), "VRAI", null),

            new Question(11,
                    "Quelle est la signification de ce panneau ?",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Véhicule autorisé seulement"),
                            new Choice("b", "Accès interdit aux piétons"),
                            new Choice("c", "Zone dangereuse")
                    ), "b", "/quiz/question-11.png"),

            new Question(12,
                    "Quelle est la signification de ce panneau ?",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Trousse de premiers soins"),
                            new Choice("b", "Safe Route to Fire Exit"),
                            new Choice("c", "Master point (point de rassemblement)")
                    ), "c", "/quiz/question-12.png"),

            new Question(13,
                    "Quelle est la signification de ce panneau ?",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Dangereux pour l'environnement"),
                            new Choice("b", "Dangereux pour l'homme"),
                            new Choice("c", "Poison")
                    ), "a", "/quiz/question-13.png"),

            new Question(14,
                    "Lequel des panneaux suivants signifie « Danger, Électricité » ?",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Panneau A"),
                            new Choice("b", "Panneau B"),
                            new Choice("c", "Panneau C"),
                            new Choice("d", "Panneau D")
                    ), "d", null),

            new Question(15,
                    "Qu'est-ce que la maladie professionnelle ?",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Maladie transmise par un collègue"),
                            new Choice("b", "Maladie causée par les conditions de travail"),
                            new Choice("c", "Maladie liée à l'ancienneté au travail")
                    ), "b", null),

            new Question(16,
                    "Quelle image a la forme et la couleur correctes pour un panneau « Interdiction » ou « Ne pas » ?",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Panneau A"),
                            new Choice("b", "Panneau B"),
                            new Choice("c", "Panneau C"),
                            new Choice("d", "Panneau D")
                    ), "c", null),

            new Question(17,
                    "La sécurité n'est qu'une question de bon sens. L'entité n'a pas besoin de perdre du temps à former les gens.",
                    QuestionType.VRAI_FAUX, List.of(), "FAUX", null),

            new Question(18,
                    "Vous n'avez pas besoin de demander la permission de la victime pour prodiguer les premiers soins.",
                    QuestionType.VRAI_FAUX, List.of(), "FAUX", null),

            new Question(19,
                    "Si la scène n'est pas sûre, il est acceptable de déplacer la victime avant de pratiquer les premiers secours.",
                    QuestionType.VRAI_FAUX, List.of(), "VRAI", null),

            new Question(20,
                    "Housekeeping (L'entretien ménager) joue un rôle important dans la prévention des glissades, trébuchements et chutes.",
                    QuestionType.VRAI_FAUX, List.of(), "VRAI", null),

            new Question(21,
                    "Lorsqu'on monte à une échelle :",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Suivez la règle des 3 points de contact"),
                            new Choice("b", "Tourne-toi dos à l'échelle pour pouvoir sauter au cas où tu glisses")
                    ), "a", null),

            new Question(22,
                    "Les types de glissades et de chutes les plus courants dans l'exploitation minière sont :",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Marcher sur les Catwalks (passerelles)"),
                            new Choice("b", "Courir sur des surfaces planes"),
                            new Choice("c", "Montage et démontage d'équipement")
                    ), "c", null),

            new Question(23,
                    "La première étape en cas d'urgence est :",
                    QuestionType.MCQ,
                    List.of(
                            new Choice("a", "Vérification du battement de cœur de la victime"),
                            new Choice("b", "Vérification de la scène pour la sécurité"),
                            new Choice("c", "Vérifier si la victime est consciente")
                    ), "b", null)
    );
}