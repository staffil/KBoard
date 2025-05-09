$(function(){
    // 추가 버튼 누르면 첨부파일 추가 함
    // 그리고 부모쪽 div 없앰  => 버튼을 클릭할떄 마다 다른 이름의 파일을 만들게 할거임
    let i =0
    $("#btnAdd").click(function(){
        $("#files").append(`
            <div class="input-group mb-2">
               <input class="form-control col-xs-3" type="file" name="upfile${i}"/>
               <button type="button" class="btn btn-outline-danger" onclick="$(this).parent().remove()">삭제</button>
            </div>
        `)
        i++;
    })
    // summernote 추가
    $('#content').summernote({
        height: '300px',

    });
});