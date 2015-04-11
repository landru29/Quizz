<?php
/**
 * Plugin Name: Quiz-Bonhommes
 * Description: Provide a quiz on roller derby rules
 * Version: 1.0.0
 * Author: Cyrille Meichel
 * Author URI: http://derby.parseapp.com
 * Text Domain: quizbonhomme
 * Domain Path: /locale/
 * Network: true
 * License: GPL2
 */



class Quiz_Bonhommes_Plugin
{
    public function __construct()
    {
        define( 'PARSE_API_KEY', 'parse-api-key');
        define( 'PARSE_API_ID', 'parse-api-id');
        define( 'QUIZ_OPTION_SENDER', 'quiz_bonhommes');
        include_once plugin_dir_path( __FILE__ ).'/services/options.php';
        include_once plugin_dir_path( __FILE__ ).'/services/widget.php';
        include_once plugin_dir_path( __FILE__ ).'/services/tinymce.php';

        // Parse.com framework
        wp_enqueue_script( 'parse.com', 'http://www.parsecdn.com/js/parse-1.3.4.min.js', '1.3.4', true );
        // Markdown lib
        wp_enqueue_script( 'markdown', plugin_dir_url( __FILE__ ) . '/js/showdown.js', '1.0.0', true );
        // Quizz script
        wp_enqueue_script( 'quiz', plugin_dir_url( __FILE__ ) . '/js/quiz.js', '1.0.0', true );
        // quizz style
        wp_enqueue_style( 'quiz', plugin_dir_url( __FILE__ ) . '/css/style.css', array(), '1.0.0');
        // add entry in the editor
        new Shortcode_Tinymce();

        // Initialize parse.com
        add_action('wp_footer', function() {
            $parseApiKey = get_option( PARSE_API_KEY, null );
            $parseApiId = get_option( PARSE_API_ID, null );
            echo '<script>Parse.initialize( "'.$parseApiId.'", "'.$parseApiKey.'");</script>';
        });

        // register the widget
        add_action('widgets_init', function() {register_widget('Quiz_Bonhommes_Widget');});

        // Add 'settings' in the plugin panel
        add_filter( 'plugin_action_links_' . plugin_basename(__FILE__), function( $links ) {
            $mylinks = array(
                '<a href="' . admin_url( 'options-general.php?page=quiz-bonhommes-key-config' ) . '">Settings</a>',
            );
            return array_merge( $links, $mylinks );
        });

        // Intance to options object
        $options = new Quiz_Bonhommes_Options();

        // Add options submenu in config panel
        add_action('admin_menu', function() {
             add_options_page('Quiz', 'Quiz', 'manage_options', 'quiz-bonhommes-key-config', array(new Quiz_Bonhommes_Options(), 'display_page') );
        });

    }

}

new Quiz_Bonhommes_Plugin();
