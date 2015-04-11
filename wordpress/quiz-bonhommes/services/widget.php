<?php
class Quiz_Bonhommes_Widget extends WP_Widget
{
    public function __construct()
    {
        parent::__construct('Quiz_Bonhommes', 'Quiz des Bonhommes', array('description' => 'Quizz pour apprendre les règles'));
        $this->id = uniqid('quiz-bonhommes-');
    }

    public function widget($args, $instance)
    {
        echo '<h1 class="quiz">Quizz !</h1>';
        echo '<div id="' . $this->id . '" class="quiz"></div>';
        echo '<script>';
        echo 'jQuery(document).ready(function(){';
        echo 'var quizBonhomme = new QuizBonhomme(jQuery(\'div#' . $this->id . '\'));';
        echo 'quizBonhomme.newQuestion();';
        echo '});';
        echo '</script>';
    }
}