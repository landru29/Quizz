<?php
class Quiz_Bonhommes_Options
{
    public function __construct()
    {
        add_action('admin_init', function() {
            register_setting(QUIZ_OPTION_SENDER, PARSE_API_KEY);
            register_setting(QUIZ_OPTION_SENDER, PARSE_API_ID);
        });
    }

    public function display_page() {
        echo '<h1>'.get_admin_page_title().'</h1>';
            ?>
            <form method="post" action="options.php">
                <?php settings_fields(QUIZ_OPTION_SENDER) ?>
                <label>Parse API ID</label>
                <input type="text" name="<?php echo PARSE_API_ID ?>" value="<?php echo get_option(PARSE_API_ID)?>"/>
                <br>
                <label>Parse API key</label>
                <input type="text" name="<?php echo PARSE_API_KEY ?>" value="<?php echo get_option(PARSE_API_KEY)?>"/>
                <?php submit_button(); ?>
            </form>
            <?php
    }
}