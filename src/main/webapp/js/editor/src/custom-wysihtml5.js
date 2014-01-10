/**
 * Overrides bootstrap-wysihtml5
 *
 *  Customized:
 *    initInsertImage, insertImage
 *    initInsertLink, insertLink
 */

var xhrFetchingImages;

bootWysiOverrides = {
  initInsertImage: function(toolbar) {
    var self = this;
    var insertImageModal = toolbar.find('.bootstrap-wysihtml5-insert-image-modal');
    var urlInput = insertImageModal.find('.bootstrap-wysihtml5-insert-image-url');
    var altInput = insertImageModal.find('.bootstrap-wysihtml5-insert-image-alt');
    var insertButton = insertImageModal.find('a.btn-primary');
    var initialValue = urlInput.val();
    var caretBookmark;
    
    var chooser = insertImageModal.find('.image_chooser.images');
    /* this is the template we put in the image dialog */
    var optionTemplate = _.template("<div class='image-item-wrapper'>" +
          "<div class='image-description'><div class='image-name'><%= file %></div>" +
          "<div class='image-holder'>" +
          "<img src='<%= url %>' title='<%= file %>'></div>" +
          "<div class='image-actions bootstrap-icons'><a href='<%= url %>' class='btn btn-small' target='_blank' title='Näytä alkuperäinen kuva'><i class='icon-picture'></i></a>&nbsp;&nbsp;<a class='btn btn-small btn-primary js-choose-image' data-src='<%= url %>' title='Valitse kuva sisältöön'><i class='icon-plus-sign icon-white'></i></a></div>" +
          "</div></div>");

    // populate chooser
    // TODO: this get's called once for each wysiwyg on the page.  we could 
    //       be smarter and cache the results after call 1 and use them later.
    if (!xhrFetchingImages) {

      var imagesURL = '/imageJson';

      $.getJSON(imagesURL, function(data) {
          $(data).each(function() {
              chooser.append(optionTemplate(this));
          });
      }).error( function() {
        chooser.html("Could not load images.");
      });
    }

    $('.js-choose-image').live('click', function(){
      urlInput.val($(this).data('src'));
    });

    $('.image-holder').live('click', function(){
      urlInput.val($(this).find('img').attr('src'));
      urlInput.data('title', $(this).find('img').attr('title'));  // Store the file name for later use
    });

    var insertImage = function() {
        var url = urlInput.val();
        var tmpAlt = urlInput.data('title').split('.')[0];        // Remove file extensions as dots will be removed by WYSIHTML5
        var alt = altInput.val();
        urlInput.val(initialValue);
        self.editor.currentView.element.focus();
        if (caretBookmark) {
          self.editor.composer.selection.setBookmark(caretBookmark);
          caretBookmark = null;
        }

        // WYSIHTML5 overrides image's title-attribute with mouseovering the image if the title is empty
        // If user leaves alt-input empty, we will use images file name as title and alt attribute
        if ( alt === '') {
          alt = tmpAlt;
        }

        self.editor.composer.commands.exec("insertImage", { src: url, alt: alt, title: alt });
    };

    urlInput.keypress(function(e) {
        if(e.which == 13) {
            insertImage();
            insertImageModal.modal('hide');
        }
    });

    insertButton.click(insertImage);

    insertImageModal.on('shown', function() {
        urlInput.focus();
    });

    insertImageModal.on('hide', function() {
        self.editor.currentView.element.focus();
    });

    toolbar.find('a[data-wysihtml5-command=insertImage]').click(function() {
        var activeButton = $(this).hasClass("wysihtml5-command-active");

        if (!activeButton) {
            self.editor.currentView.element.focus(false);
            caretBookmark = self.editor.composer.selection.getBookmark();
            insertImageModal.appendTo('body').modal('show');
            insertImageModal.on('click.dismiss.modal', '[data-dismiss="modal"]', function(e) {
                e.stopPropagation();
            });
            return false;
        }
        else {
            return true;
        }
    });
  },
  initInsertLink: function(toolbar) {
    var self = this;
    var insertLinkModal = toolbar.find('.bootstrap-wysihtml5-insert-link-modal');
    var urlInput = insertLinkModal.find('.bootstrap-wysihtml5-insert-link-url');
    var relInput = insertLinkModal.find('.bootstrap-wysihtml5-insert-link-rel');
    var insertButton = insertLinkModal.find('a.btn-primary');
    var initialValue = urlInput.val();
    var caretBookmark;

    var insertLink = function() {
        var url = urlInput.val();
        var target = "_blank";
        var title = "Aukeaa uuteen ikkunaan";
        var rel = "external";
        var isExternal = (relInput.attr('checked') === 'checked');
        urlInput.val(initialValue);
        self.editor.currentView.element.focus();

        if (caretBookmark) {
          self.editor.composer.selection.setBookmark(caretBookmark);
          caretBookmark = null;
        }

        if (isExternal === true) {
          self.editor.composer.commands.exec("createLink", {
              href: url,
              title: "",
              class: "external"
          });
          /*  Could be enabled when editor supports custom attributes
              https://github.com/xing/wysihtml5/issues/349
          self.editor.composer.commands.exec("createLink", {
              href: url,
              target: target,
              rel: rel,
              title: title,
              class: "external trigger-tooltip"
          });
          */
        } else {
          self.editor.composer.commands.exec("createLink", {
              href: url
          });
        }
    };
    var pressedEnter = false;

    urlInput.keypress(function(e) {
        if(e.which == 13) {
            insertLink();
            insertLinkModal.modal('hide');
        }
    });

    insertButton.click(insertLink);

    insertLinkModal.on('shown', function() {
        urlInput.focus();
    });

    insertLinkModal.on('hide', function() {
        self.editor.currentView.element.focus();
    });

    toolbar.find('a[data-wysihtml5-command=createLink]').click(function() {
        var activeButton = $(this).hasClass("wysihtml5-command-active");

        if (!activeButton) {
            self.editor.currentView.element.focus(false);
            caretBookmark = self.editor.composer.selection.getBookmark();
            insertLinkModal.appendTo('body').modal('show');
            insertLinkModal.on('click.dismiss.modal', '[data-dismiss="modal"]', function(e) {
                e.stopPropagation();
            });
            return false;
        }
        else {
            return true;
        }
    });
  }
};

$.extend($.fn.wysihtml5.Constructor.prototype, bootWysiOverrides);