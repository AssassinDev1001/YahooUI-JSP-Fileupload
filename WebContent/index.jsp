<style>
#filelist {
    margin-top: 15px;
}

#uploadFilesButtonContainer, #selectFilesButtonContainer, #overallProgress {
    display: inline-block;
}

#overallProgress {
    float: right;
}
</style>
<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css">
<div id="uploaderContainer">
    <div id="selectFilesButtonContainer">
    </div>
    <div id="uploadFilesButtonContainer">
      <button type="button" id="uploadFilesButton"
              class="yui3-button" style="width:250px; height:35px;">Upload Files</button>
    </div>
    <div id="overallProgress">
    </div>
</div>

<div id="filelist">
  <table id="filenames">
    <thead>
       <tr>
       		<th>File name</th>
       		<th>File size</th>
       		<th>Date</th>
       		<th>Type</th>
       		<th>Description</th>
       		<th>Percent uploaded</th>
       </tr>
       <tr id="nofiles">
        <td colspan="6">
            No files have been selected.
        </td>
       </tr>
    </thead>
    <tbody>
    </tbody>
  </table>
</div>

<script src="http://yui.yahooapis.com/3.18.1/build/yui/yui-min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script src="js/jquery-ui.min.js"></script>
<script>

YUI({filter:"raw"}).use("uploader", function(Y) {
	Y.one("#overallProgress").set("text", "Uploader type: " + Y.Uploader.TYPE);
   if (Y.Uploader.TYPE != "none" && !Y.UA.ios) {
       var uploader = new Y.Uploader({width: "250px",
                                      height: "35px",
                                      multipleFiles: true,
                                      //swfURL: "flashuploader.swf?t=" + Math.random(),
                                      uploadURL: "UploadServlet",
                                      simLimit: 2,
                                      withCredentials: false
                                     });
       
       var uploadDone = false;

       uploader.render("#selectFilesButtonContainer");

       uploader.after("fileselect", function (event) {

          var fileList = event.fileList;
          var fileTable = Y.one("#filenames tbody");
          if (fileList.length > 0 && Y.one("#nofiles")) {
            Y.one("#nofiles").remove();
          }

          if (uploadDone) {
            uploadDone = false;
            fileTable.setHTML("");
          }
          
          Y.each(fileList, function (fileInstance) {
	              fileTable.append("<tr id='" + fileInstance.get("id") + "_row" + "'>" +
	                               "<td class='filename'>" + fileInstance.get("name") + "</td>" +
	                               "<td class='filesize'>" + fileInstance.get("size") + "</td>" +
	                               "<td class='date'><input type = 'text' class = 'datepicker'></td>" +
	                               "<td class='type'><input type = 'text' maxlength = '250'></td>" +
	                               "<td class='description'><input type = 'text' maxlength = '250'></td>" +
	                               "<td class='percentdone'>Hasn't started yet</td>");
	             jQuery(".datepicker" ).datepicker();
	             jQuery(".datepicker" ).datepicker( "option", "dateFormat", 'yy-mm-dd');
			});
                    
       });

       uploader.on("uploadprogress", function (event) {
            var fileRow = Y.one("#" + event.file.get("id") + "_row");
                fileRow.one(".percentdone").set("text", event.percentLoaded + "%");
       });

       uploader.on("uploadstart", function (event) {
            uploader.set("enabled", false);
            Y.one("#uploadFilesButton").addClass("yui3-button-disabled");
            Y.one("#uploadFilesButton").detach("click");
       });

       uploader.on("uploadcomplete", function (event) {
            var fileRow = Y.one("#" + event.file.get("id") + "_row");
            if(event.data == '__invalid__')
            {
				fileRow.one(".percentdone").set("text", "Invalid File Type!");
				return;
            }
            fileRow.one(".percentdone").set("text", "Finished!");
			
			var data = {};
			//data['file_name'] = jQuery("#" + event.file.get("id") + "_row").children().eq(0).text();// + event.file.get("id");
			data['file_name'] = event.data;
			data['size'] = jQuery("#" + event.file.get("id") + "_row").children().eq(1).text();
			data['date'] = jQuery("#" + event.file.get("id") + "_row").children().eq(2).children().eq(0).val();
			data['type'] = jQuery("#" + event.file.get("id") + "_row").children().eq(3).children().eq(0).val();
			data['description'] = jQuery("#" + event.file.get("id") + "_row").children().eq(4).children().eq(0).val();
			$.post("/JSPConnectToMySQL/SaveData",
			  data,
			  function(data){
			  }
			);
                
       });

       uploader.on("totaluploadprogress", function (event) {
                Y.one("#overallProgress").setHTML("Total uploaded: <strong>" + event.percentLoaded + "%" + "</strong>");
       });

       uploader.on("alluploadscomplete", function (event) {
                     uploader.set("enabled", true);
                     uploader.set("fileList", []);
                     Y.one("#uploadFilesButton").removeClass("yui3-button-disabled");
                     Y.one("#uploadFilesButton").on("click", function () {
                          if (!uploadDone && uploader.get("fileList").length > 0) {
                             uploader.uploadAll();
                          }
                     });
                     Y.one("#overallProgress").set("text", "Uploads complete!");
                     uploadDone = true;
                    
       });

       Y.one("#uploadFilesButton").on("click", function () {
         if (!uploadDone && uploader.get("fileList").length > 0) {
            uploader.uploadAll();
         }
       });
   }
   else {
       Y.one("#uploaderContainer").set("text", "We are sorry, but to use the uploader, you either need a browser that support HTML5 or have the Flash player installed on your computer.");
   }
});

</script>