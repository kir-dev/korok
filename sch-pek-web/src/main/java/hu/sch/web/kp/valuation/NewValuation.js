$(function (){
    $("form").submit(function () {
        // no need to re-enable the button, because form submit initiates a new full request cycle.
        $(this).find('input[type=submit]').prop('disabled', true);
    });
});
