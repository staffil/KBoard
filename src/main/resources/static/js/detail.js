$(function(){
    // 글 [삭제] 버튼
    $("#btnDel").click(function(){
        confirm("삭제하시겠습니까?") && $("form[name='frmDelete']").submit();
    });

    // status 는 현재 글의 상태
    // 현재 글의 id 값
    const id = $("input[name='id']").val().trim();

    // 현재 글의 댓글을 불러온다
    loadComment(id);

    // 댓글 작성 버튼 누르면 댓글 등록 하기.
    // 1. 어느글에 대한 댓글인지? --> 위에 id 변수에 담겨있다
    // 2. 어느 사용자가 작성한 댓글인지? --> logged_id 값
    // 3. 댓글 내용은 무엇인지?  --> 아래 content
    $("#btn_comment").click(function(){
        const content = $("#input_comment").val().trim();

        // 검증
        if(!content){
            alert("댓글 입력을 하세요");
            $("#input_comment").focus();
            return;
        }

        // 전달할 parameter 들 준비
        const data = {
            "post_id": id,
            "user_id": logged_id,
            "content": content
        };

        fetch("/comment/write", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `post_id=${id}&user_id=${logged_id}&content=${content}`,
        }).then(response => response.json())
            .then(data => {
                if (data.status == "Ok") {
                    alert(data.status);
                    return;
                }
                loadComment(id);
                $("#input_comment").val('');
            })

    });


});

// 특정 글(post_id) 의 댓글 목록 읽어오기
function loadComment(post_id){

    fetch("/comment/list/" + post_id)
        .then(response => response.json())
        .then(data => {
            if(data.status !== "OK"){
                alert(data.status);
                return;
            }

            buildComment(data);   //  댓글 화면 렌더링.
            // ★댓글목록을 불러오고 난뒤에 삭제에 대한 이벤트 리스너를 등록해야 한다
            addDelete();
        })

}

function buildComment(result){
    $("#cmt_cnt").text(result.count);

    const out = [];

    result.data.forEach(comment => {
        let id = comment.id;
        let content = comment.content.trim();
        let regdate = comment.regdate;

        let user_id = parseInt(comment.user.id);
        let username = comment.user.username;
        let name = comment.user.name;

        // 삭제버튼 여부 : 작성자 본인인 경우만 삭제번튼 보이게 하기.
        const delBtn = (logged_id !== user_id) ? '' : `
            <i class="btn fa-solid fa-delete-left text-danger" data-bs-toggle="tooltip" 
                data-cmtdel-id="${id}" title="삭제"></i>
        `;

        const row = `
        <tr>
            <td><span><strong>${username}</strong><br><small class="text-secondary">(${name})</small></span></td>
            <td>
                <span>${content}</span>
                ${delBtn}
            </td>
            <td><span><small class="text-secondary">${regdate}</small></span></td>
        </tr>        
        `;

        out.push(row);
    });

    $("#cmt_list").html(out.join("\n"));
} // end buildComment();

// 댓글 삭제버튼이 눌렸을때. 해당 댓글 삭제하는 이벤트를 삭제버튼에 등록
function addDelete(){

    // 현재 글의 id  (댓글 삭제후에  다시 댓글 목록 불러와야 하기 때문에 필요하다)
    const id = $("input[name='id']").val().trim();

    $("[data-cmtdel-id]").click(function(){
        if(!confirm("댓글을 삭제하시겠습니까?")) return;

        // 삭제할 댓글의 id
        const comment_id = $(this).attr("data-cmtdel-id");

        fetch("/comment/delete", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `id=${comment_id}`,
        }).then(response => response.json())
            .then(data => {
                if(data.status !== "OK"){
                    alert(data.status);
                    return;
                }
                // 삭제후에도 다시 댓글 목록 불러와서 업데이트 해야 함.
                loadComment(id);
            })
    });


}



















