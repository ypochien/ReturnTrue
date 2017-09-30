var ReturnTrue = ReturnTrue || {};
ReturnTrue.stationNameDict = {
    "ST_D8NNN9ZK" : "罗马",
    "ST_ENZZ7QVN" : "威尼斯",
    "ST_EZVVG1X5" : "米兰",
};
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
        var email = $("#email").val(),
            tags = $("#tags").val(),
            start = $("#start").val(),
            end = $("#end").val(),
            data = { 
                "Email": email, 
                "Tag": tags, 
                "Start" : start, 
                "End": end  
            };
            console.log(data);
            
        $.ajax({
            url: "/api/book",
            method: "POST",
            data: data
        }).done(function(d) {
            swal({
                title: "購票成功",
                type: 'success',
                html:
                    '<p>訂單編號 :' + d.Id + "<p>" + 
                    '<p>起始站 :' + ReturnTrue.stationNameDict[d.Start] + "<p>" + 
                    '<p>終點站 :' + ReturnTrue.stationNameDict[d.End] + "<p>" + 
                    '<p>票價 :' + d.TicketPrice + "<p>" + 
                    '<p>您的 Email : ' + d.UserInfo.Email + "<p>" ,
                showCloseButton: true,
                showCancelButton: true,
            });
            ReturnTrue.blockUI.unblock();
        }).fail(function(jqXHR, textStatus){
            swal({
                title: "購票失敗",
                type: 'error',
                showCloseButton: true,
                showCancelButton: true,
            });
            ReturnTrue.blockUI.unblock();
        });
        
    });
};
