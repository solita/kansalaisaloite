/**
 * Handle WYSIHTML5 editor actions
 * 
 * 		Edit (initialize and load editor)
 * 		Save editor content
 * 		Cancel editing
 *		Upload image
 *		Publish draft / restore published
 */

(function($) {

	var	$btnEdit			= $('.js-wysihtml5-edit'),
		$btnSave			= $('.js-wysihtml5-save'),
		$editModeButtons	= $('.editor-buttons.edit-mode'),
		$viewModeButtons	= $('.editor-buttons.view-mode'),
		$editor				= $('#wysihtml5-editor'),
		$editorContainer	= $('#wysihtml5-container'),
		$contentEditable	= $('#content-editable'),
		$mainHeading		= $('h1'),
		hideClass			= 'js-hide';

	// override options
	var wysiwygOptions = {
	  	locale: "fi-FI",
        classes: {
                "external" : 1,
                //"trigger-tooltip" : 1,	// enable only when title-attribute is supported
                "wysiwyg-color-date" : 1
            },
	    customTags: {
	   	  // these are set in boostrap-wysihtml.js as they does not seem to work when set here
	      "p": {},
	      "h4": {},
	      "em": {},
	      "strong": {},
	      "hr": {},
	      "img": {
                "check_attributes": {
                    "width": "numbers",
                    "alt": "alt",
                    "src": "url",
                    "height": "numbers",
                    "title": "alt"
                }
            }
	    },
	    customStyles: {
	      // keys with null are used to preserve items with these classes, but not show them in the styles dropdown
	      'shrink_wrap': null,
	      'credit': null,
	      'tombstone': null,
	      'chat': null,
	      'caption': null
	    },
	    customTemplates: {
    	  "font-styles": function(locale, options) {
            var size = (options && options.size) ? ' btn-'+options.size : '';
            return "<li class='dropdown'>" +
              "<a class='btn dropdown-toggle" + size + "' data-toggle='dropdown' href='#' title='" + locale.font_styles.title + "'>" +
              "<i class='icon-font'></i>&nbsp;<span class='current-font'>" + locale.font_styles.normal + "</span>&nbsp;<b class='caret'></b>" +
              "</a>" +
              "<ul class='dropdown-menu'>" +
                "<li><a data-wysihtml5-command='formatBlock' data-wysihtml5-command-value='div' tabindex='-1'>" + locale.font_styles.normal + "</a></li>" +
                "<li><a data-wysihtml5-command='formatBlock' data-wysihtml5-command-value='p' tabindex='-1'>" + locale.font_styles.paragraph + "</a></li>" +
                "<li><a data-wysihtml5-command='formatBlock' data-wysihtml5-command-value='h2' tabindex='-1'>" + locale.font_styles.h2 + "</a></li>" +
                "<li><a data-wysihtml5-command='formatBlock' data-wysihtml5-command-value='h3' tabindex='-1'>" + locale.font_styles.h3 + "</a></li>" +
                "<li><a data-wysihtml5-command='formatBlock' data-wysihtml5-command-value='h4' tabindex='-1'>" + locale.font_styles.h4 + "</a></li>" +
                "<li><a data-wysihtml5-command='foreColor' data-wysihtml5-command-value='date' tabindex='-1'>" + locale.font_styles.date + "</a></li>" +
              "</ul>" +
            "</li>";
          },
	      "image": function(locale) {
	        return "<li>" +
	          "<div class='bootstrap-wysihtml5-insert-image-modal modal fade large' style='bottom: initial;'>" +
	          "<div class='modal-header'>" +
	          "<a class='close' data-dismiss='modal'>&times;</a>" +
	          "<h4>" + locale.image.insert + "</h4>" +
	          "</div>" +
	          "<div class='modal-body'>" +
	          "<div class='chooser_wrapper'>" +
	          "<div class='image_chooser images'></div>" +
	          "</div>" +
	          "</div>" +
	          "<div class='modal-footer'>" +
	          "<label>Kuvan osoite</label><input type='text' value='http://' class='bootstrap-wysihtml5-insert-image-url input-xlarge'><br/>" +
	          "<label>Kuvausteksti</label><input type='text' required value='' class='bootstrap-wysihtml5-insert-image-alt input-xlarge'>" +
	          "<div class='modal-buttons'><a href='#' class='btn btn-primary' data-dismiss='modal'>" + locale.image.insert + "</a>" +
	          "<a href='#' class='btn' data-dismiss='modal'>" + locale.image.cancel + "</a></div>" +
	          "</div>" +
	          "</div>" +
	          "<a class='btn' data-wysihtml5-command='insertImage' title='" + locale.image.insert + "'><i class='icon-picture'></i></a>" +
	          "</li>";
	      },
	      "link": function(locale, options) {
            var size = (options && options.size) ? ' btn-'+options.size : '';
            return "<li>" +
              "<div class='bootstrap-wysihtml5-insert-link-modal modal fade' style='bottom: initial'>" +
                "<div class='modal-header'>" +
                  "<a class='close' data-dismiss='modal'>&times;</a>" +
                  "<h4>" + locale.link.insert + "</h4>" +
                "</div>" +
                "<div class='modal-body'>" +
                  "<p><label>" + locale.link.url +" <span>" + locale.link.urlExample +"</span><input type='text' value='http://' class='bootstrap-wysihtml5-insert-link-url input-xlarge'></label></p>" +
                  "<p><label><input type='checkbox' class='bootstrap-wysihtml5-insert-link-rel' value='external'>" + locale.link.external +"</label></p>" +
                "</div>" +
                "<div class='modal-footer'>" +
               	  "<a href='#' class='btn btn-primary' data-dismiss='modal'>" + locale.link.insert + "</a>" +
                  "<a href='#' class='btn' data-dismiss='modal'>" + locale.link.cancel + "</a>" +
                "</div>" +
              "</div>" +
              "<a class='btn" + size + "' data-wysihtml5-command='createLink' title='" + locale.link.insert + "' tabindex='-1'><i class='icon-share'></i></a>" +
            "</li>";
          }
	    }
	  };

	var loadEditor = function(){
	    $('#wysihtml5-editor').each(function() {
		    $(this).wysihtml5(
		    	$.extend(
		    		wysiwygOptions,
		    		{
		    			html:true,
		    			color:false,
		    			stylesheets:["/js/editor/src/editor.css"]
		    		}
	    		)
	    	);

		});
	};

	$btnEdit.click(function(){
	    $viewModeButtons.addClass(hideClass);
	    $editModeButtons.removeClass(hideClass);

	    loadEditor();
	    
		$mainHeading.addClass('js-hide');

	    $editorContainer.removeClass(hideClass);
	    $contentEditable.addClass(hideClass);

	    $(prettyPrint);
	});

	$('.js-submit').click(function(){
		var btn 	= $(this),
			form 	= $("#"+btn.data('form'));

		form.submit();
	});

	$('.js-upload-image').click(function(){
		$('.bootstrap-upload-image').appendTo('body').modal('show')
	    .on('click.dismiss.modal', '[data-dismiss="modal"]', function(e) {
	        e.stopPropagation();
	    });
    });

	$('.js-publish-draft').click(function(){
		$('.bootstrap-publish-draft').appendTo('body').modal('show')
	    .on('click.dismiss.modal', '[data-dismiss="modal"]', function(e) {
	        e.stopPropagation();
	    });
    });

    $('.js-restore-published').click(function(){
		$('.bootstrap-restore-published').appendTo('body').modal('show')
	    .on('click.dismiss.modal', '[data-dismiss="modal"]', function(e) {
	        e.stopPropagation();
	    });
    });

})(jQuery);

