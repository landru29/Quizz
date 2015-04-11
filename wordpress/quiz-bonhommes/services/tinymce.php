<?php
class Shortcode_Tinymce
{
    public function __construct()
    {
        add_action('admin_init', array($this, 'shortcode_button'));
        add_action('init', array($this, 'display'));
    }

    public function shortcode_button()
    {
        if( current_user_can('edit_posts') &&  current_user_can('edit_pages') )
            {
                add_filter( 'mce_external_plugins', array($this, 'add_buttons' ));
                add_filter( 'mce_buttons', array($this, 'register_buttons' ));
            }
    }

    public function add_buttons( $plugin_array )
    {
        $plugin_array['quiz_bonhommes'] = plugin_dir_url(dirname( __FILE__ )) . 'js/tinymce-button.js';
        return $plugin_array;
    }

    public function register_buttons( $buttons )
    {
        array_push( $buttons, 'separator', 'quiz_bonhommes' );
        return $buttons;
    }

    public function display()
    {
        add_shortcode('Quiz_bonhommes', function(){
            $id = uniqid('quiz-bonhommes-');
            echo '<div id="' . $id . '" class="quiz"></div>';
            echo '<script>';
            echo 'jQuery(document).ready(function(){';
            echo 'quizBonhomme = new QuizBonhomme(jQuery(\'div#' . $id . '\'));';
            echo 'quizBonhomme.newQuestion();';
            echo '});';
            echo '</script>' . "\n";
        });
    }

}