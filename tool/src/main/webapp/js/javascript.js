var submitting = false;

function filterParticipantsList(textbox)
{
    $('#' + textbox.id + ' option').each(function (index, val)
    {
        if(val.text.toLowerCase().indexOf(textbox.value.toLowerCase()) == -1)
        {
            if(!$(this).parent().is('span'))
                $(this).wrap("<span>").hide();
        }
        else
        {
            var span = $(this).parent();
            var opt = this;
            if($(this).parent().is('span'))
            {
                $(opt).show();
                $(span).replaceWith(opt);
            }

        }
    });
}

$(document).ready(function () {

    $("#startDatePicker").datepicker( {
        dateFormat : 'dd-mm-yy'
    });

    /*the activity is started - we can not modify the room type or the media quality */
    if ($('#canModifyRoomType').val() == 1) {
        $('#roomType').prop('disabled', true);
        $('#mediaQuality').prop('disabled', true);
    }
    /*the activity is finished - we can not modify anything except users */
    if ($('#canModifyAll').val() == 1) {
        $('#title').prop('disabled', true);
        $('#description').prop('disabled', true);
        $('#startDatePicker').prop('disabled', true);
        $('#startTime').prop('disabled', true);
        $('#permanent').prop('disabled', true);
        $('#duration').prop('disabled', true);
        $('#recordingMode').prop('disabled', true);
        $('#recordingBehavior').prop('disabled', true);
        $('#recordingIsPublic').prop('disabled', true);
        $('#waitingRoom').prop('disabled', true);
        $('#ddlReminder').prop('disabled', true);
        $('#chkSendInvitation').prop('disabled', true);
        $('#chkIsNewVia').prop('disabled', true);
    }

    /* if the activity is permanent the time and duration options are disabled */
    if ($('#permanent').prop('checked')) {
        $('#startDatePicker').prop('disabled', true);
        $('#startTime').prop('disabled', true);
        $('#duration').prop('disabled', true);
        $('#ddlReminder').prop('disabled', true);
    }

    $('#permanent').change(function () {
        if ($(this).prop('checked')) {
            $('#startDatePicker').prop('disabled', true);
            $('#startTime').prop('disabled', true);
            $('#duration').prop('disabled', true);
        $('#ddlReminder').prop('disabled', true);
        }
        else {
            $('#startDatePicker').prop('disabled', false);
            $('#startTime').prop('disabled', false);
            $('#duration').prop('disabled', false);
            $('#ddlReminder').prop('disabled', false);
        }
    });

    /* if the is no recording the other recording options are disabled */
    /* onload */
    if ($("#recordingMode").val() == 0) {
        $('#recordingBehavior').prop('disabled', true);
        $('#recordingIsPublic').prop('disabled', true);
    }
    /* on change */
    $("#recordingMode").on('change', function () {
        if ($(this).val() == 0) {
            $('#recordingBehavior').prop('disabled', true);
            $('#recordingIsPublic').prop('disabled', true);
        }
        else {
            $('#recordingBehavior').prop('disabled', false);
            $('#recordingIsPublic').prop('disabled', false);
        }
    });

    /* onload */
    if($('#site').length > 0)
        if ($('#site')[0].checked) 
        {
            $("#site").prop("checked", true);
            $("#groupEnrollment").hide();
            $("#divMembers").hide();
        }
    
    if($('#manual').length > 0)
        if ($('#manual')[0].checked) 
        { 
            $("#manual").prop("checked", true);
            $("#groupEnrollment").hide();
            $("#divMembers").show();
        }
    
    if($('#group').length > 0)
        if ($('#group')[0].checked) 
        {
            $("#group").prop("checked", true);
            $("#groupEnrollment").show();
            $("#divMembers").hide();
        }
    
    
    /* on change */
    $("input[type='radio']").change(function () {
    
        $('#animators option').each(function () {
            $('form[name="activityForm"]').append('<input type="text" style="display:none;" name="hiddenAnimators"' + $(this).val() + '" value="' + $(this).val() + '" />');
        });
    
        $('form[name="activityForm"]').submit();
    });

    $(".groups").change(function () {
        $('form[name="activityForm"]').append('<input type="text" style="display:none;"  name="type" value="group" />'); 
        
        if ($('form[name="activityForm"] input:checkbox:checked').length > 0) {
            $('.groups').each(function () {
                if ($(this).prop('checked')) {
                    $('form[name="activityForm"]').append('<input type="text" style="display:none;"  name="groups"' + $(this).val() + '" value="' + $(this).val() + '" />');   
                }
            });
            
            $('#animators option').each(function () {
                $('form[name="activityForm"]').append('<input type="text" style="display:none;" name="hiddenAnimators"' + $(this).val() + '" value="' + $(this).val() + '" />');
            });
            
        }        
        
        $('form[name="activityForm"]').submit();
        
    });

    /* onload */
    $("#addPresentor").addClass('disabled');
    $('#addPresentor').unbind('click');

    /* on change */
    $("#participants").click(function () {
        var count = $("#participants :selected").length;
   
        if (count == 1) { /* link is only active when only 1 user is selected */
                $("#addPresentor").removeClass('disabled');
                $('#addPresentor').bind('click');
            }
            else {
                $("#addPresentor").addClass('disabled');
                $('#addPresentor').unbind('click');
            }
        
        $("#animators option").removeAttr("selected");
                
    });
    
    $("#animators").click(function () {      
        $("#participants option").removeAttr("selected");
        $("#addPresentor").addClass('disabled');

    });


    $('#addPresentor').click(function () {
       
        $('#participants option').each(function () {
            if ($(this).is(':selected')) {
                $('#presentor option').remove().appendTo('#animators');
                return !$(this).remove().appendTo('#presentor');
            }
        });
         $("#addPresentor").addClass('disabled');   
         $('div.divPresenterArrow').removeClass('border');
         
    });
    
    $(".tools").click(function () { 
        $("#addPresentor").addClass('disabled');
    });
    
    //Participants to animator
    $('#addPToA').click(function () { 
       return !$('#participants option:selected').remove().appendTo('#animators');
    });
    $('#removePToA').click(function () {
        return !$('#animators option:selected').remove().appendTo('#participants');
    });
    $('#addAllPToA').click(function () {
        return !$('#participants option').remove().appendTo('#animators');
    });
    $('#removeAllPToA').click(function () {
        return !$('#animators option').remove().appendTo('#participants');
    });
    
    //Members to participants.
    $('#addMToP').click(function () { 
       return !$('#members option:selected').remove().appendTo('#participants');
    });
    $('#removeMToP').click(function () {
        return !$('#participants option:selected').remove().appendTo('#members');
    });
    $('#addAllMToP').click(function () {
        return !$('#members option').remove().appendTo('#participants');
    });
    $('#removeAllMToP').click(function () {
        return !$('#participants option').remove().appendTo('#members');
    });
    
    try
    {
        top.window.onresize = function(){if($("iframe")[0] != undefined)$("iframe")[0].style.width = $(document).width()-200 + "px";};
        top.document.getElementsByClassName("portletMainIframe")[0].style.minWidth = "790px";
    }
    catch (e) {
    
    }
    
});

function validateBeforeSubmit(){
    var enrollmentType = document.forms["activityForm"]["enrollmentType"].value;
    var groupSelected=false;

    if (enrollmentType == '2'){
        var groups =document.forms["activityForm"]["groups"];

        if (typeof groups.length !== 'undefined'){
            for(var i=0,l=groups.length;i<l;i++){
                    if(groups[i].checked){
                        groupSelected=true;
                        break;
                    }
            }
        } else if (typeof groups !== 'undefinded'){
             if(groups.checked){
                groupSelected=true;
             }
        }
        if (!groupSelected){
            $("p#warning").removeClass("hide");
            $("#groupEnrollment").css('color','red');
            $('html,body').scrollTop(0);
        }
    }else{
        groupSelected=true;
    }

    if (groupSelected){
        $("p#warning").addClass("hide");
        validate();
    }

}

function validate(){

    if(submitting)
    {
        return false;
    }

    var perm = $('#permanent').prop('checked');
    
    $("#lblTitle").css('color','black');
    $("#lblDuration").css('color','black');
    $("#lblStartTime").css('color','black');
    
    if (!$("#title").val() || !$("#presentor").val())
    {
        $("#title").focus();
        $("#title").select();
        $("#lblTitle").css('color','red');
    }
    else if(perm == false && (!$("#duration").val() ||  !$('#duration').val().match("^[1-9][0-9]{0,2}$")))
    {
        $("#duration").focus();
        $("#duration").select();        
        $("#lblDuration").css('color','red');
    }
    else if(perm == false && (!$("#startTime").val() || !$("#startTime").val().match("^([01]?[0-9]|2[0-3])[:][0-5][0-9]$")))
    {
        $("#startTime").focus();
        $("#startTime").select();
        $("#lblStartTime").css('color','red');
    }
   else
   {
   
        $("p#warning").addClass("hide");
        
        $("<input>").attr({type: "hidden",id: "save", name: "save"}).appendTo('form[name="activityForm"]');
        
        $('#animators option').each(function () {
            $('form[name="activityForm"]').append('<input type="text" style="display:none;" name="hiddenAnimators"' + $(this).val() + '" value="' + $(this).val() + '" />');
        });
        
        $('#participants option').each(function () {
            $('form[name="activityForm"]').append('<input type="text" style="display:none;" name="hiddenParticipants"' + $(this).val() + '" value="' + $(this).val() + '" />');
        });
        
        $('form[name="activityForm"]').append('<input type="text" style="display:none;" name="type" value="null" />'); 
        
        submitting = true;
        $("#btnSave").val("Enregistrement en cours...");
        $("#btnCancel").attr("style","display:none;");
        
        $('form[name="activityForm"]').submit();
        return true;
  }
  
       $("p#warning").removeClass("hide");
       
       return false;    
  
}
