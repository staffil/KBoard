$(function(){
    // 페이징 헤더
    $("[name='pageRows']").change(function(){
        $("[name='frmPageRows']").attr({
            "method": "POST",
            "action": "pageRows",
        }).submit();
    });
});
// 이거 좀 이해 안됨*****