var ReturnTrue = ReturnTrue || {};
ReturnTrue.blockUI = {
    block: function () {
        $.blockUI({
            css: {
                border: 'none',
                opacity: 0
            },
            message: '<h4 style="height:100px;"></h4>',
            overlayCSS: {
                background: 'url(../Content/loading.gif) #000 no-repeat center center',
            }
        });
    },
    unblock: function () {
        $.unblockUI();
    }
};
ReturnTrue.getFormData = function($form){
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function(n, i){
        indexed_array[n['name']] = n['value'];
    });
    return indexed_array;
};
ReturnTrue.initPage = function(){
    $("body").on("click", ".order-submit", function(){
        ReturnTrue.blockUI.block();
        var $form = $(".order-form");
        var orderData = ReturnTrue.getFormData($form);
        // console.log(orderData);
        $.ajax({
            url: "/api/book",
            method: "POST",
            data: { "Email": "test@test.com", "Tag": "test" }
        }).done(function(d) {
            console.log(d);
            swal({
                title: "購票成功",
                type: 'success',
                html:
                  '<p>起始站' + d.Start + "<p>" + 
                  '<p>終點站' + d.End + "<p>" + 
                  '<p>票價' + d.TicketPrice + "<p>" + 
                  '<p>您的 Email : ' + d.UserInfo.Email + "<p>" ,
                showCloseButton: true,
                showCancelButton: true,
            });
            ReturnTrue.blockUI.unblock();
        }).fail(function(jqXHR, textStatus){
            console.log(jqXHR);
            console.log(textStatus);
            ReturnTrue.blockUI.unblock();
        });
    });
};
