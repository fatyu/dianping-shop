/**
 * 创建文本编辑器工具
 * 	依赖jquery
 */
var ctx = '';
if($('script[editor-ctx]').length > 0) {
	ctx = $('script[editor-ctx]').attr('editor-ctx');
}
var editor = function($dom) {
	var toolbar = [ 'title', 'bold', 'italic', 'underline', 'color', '|',
		'ol', 'ul', 'blockquote', '|', 'link', 'image', 'hr' , '|', 'indent', 'outdent', 'alignment' ];
	var editor = new Simditor({
		textarea : $dom,
		placeholder : '这里输入文字...',
		toolbar : toolbar,
		defaultImage : 'http://img.wangyuhudong.com/wy.jpg',
		upload : {
			url : ctx + '/common/upload'
		}
	});
	
	function initSimditorPlaceholder() {
		$('.simditor-toolbar').css('width', 'initial');
		$('.simditor-placeholder').each(function() {
			$(this).css('top', $(this).siblings('.simditor-toolbar').height() + 2);
		});
	}
	
	var $parentModal = $dom.parents('.modal');
	if($parentModal.length > 0) {
		$parentModal.on('shown.zui.modal', function() {
			initSimditorPlaceholder();
		})
	}
	
	return editor;
}
var setEditorText = function(editor, value) {
	editor.setValue(value);
}