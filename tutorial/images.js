var imageDialog;

$(document).ready(function() {
    imageDialog =$("<div id='dialog'><img height='700px' id='image' src=''/></div>").dialog({
      modal: true,
      resizable: false,
      draggable: false,
      width: "450px",
      autoOpen: false
    });
       
    $('.screenshot img').click(function(event){
      event.preventDefault();
      PreviewImage($(this).attr('src'));                                 
    });
    $('#navigtitle').css("visibility", "visible")
});

PreviewImage = function(uri) {
  uri = "large/" +uri;
  //Get the HTML Elements
  imageTag = $('#image');
 
  //Split the URI so we can get the file name
  uriParts = uri.split("/");

  //When the image has loaded, display the dialog
  imageTag.load(function(){
    imageDialog.dialog("open");
  });

  //Set the image src
  imageTag.attr('src', uri);
}