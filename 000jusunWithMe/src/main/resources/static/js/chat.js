// 텍스트 입력 시 높이 자동 조절 (최대 100px)
// js파일을 따로 분리하면서 로컬이 아닌 전역 스코프로 수정
function resizeInput(textarea) {
    textarea.style.height = 'auto';
    textarea.style.height = (textarea.scrollHeight > 100 ? 100 : textarea.scrollHeight) + 'px';
}

$(function () {
    var socket = new SockJS('/websocket');
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/messages', function (messageOutput) {
            showMessage(JSON.parse(messageOutput.body));
        });
    });

    // 입력한 메세지 전송버튼 클릭 시 서버로 전송
    function sendMessage() {
        const input = $('.chat-input');
        const message = input.val();
        if (message.trim() !== '') {
            var chatMessage = {
                sentTime: new Date(),
                content: message,
                msgFname: '',
                msgType: 1,
                userNo: 1236,
                groupNo: 111
            };
            stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
            input.val('');
            input.css('height', 'auto');
        }
    }

    // 수신한 메세지 채팅창에 표시
    function showMessage(message) {
        const chatContainer = $('#chatContainer');
        const chatMessage = $('<div>').addClass('chat-message').addClass(message.userNo === 1236 ? 'my-chat' : 'friend-chat');
        chatMessage.html(`
            <div class="chat-bubble-container ${message.userNo === 1236 ? 'my-chat' : 'friend-chat'}">
                <div class="chat-bubble ${message.userNo === 1236 ? 'my-chat' : 'friend-chat'}">${message.content.replace(/\n/g, '<br>')}</div>
                <div class="timeAndRead ${message.userNo === 1236 ? 'my-chat' : 'friend-chat'}">
                    <div class="unread">2</div>
                    <div class="time">오후 ${new Date(message.sentTime).getHours()}:${new Date(message.sentTime).getMinutes()}</div>
                </div>
            </div>
        `);
        chatContainer.append(chatMessage);
        chatContainer.scrollTop(chatContainer[0].scrollHeight);
    }

    // 이미지 파일 선택 시 채팅창에 작게 표시
    function previewImage(event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                const chatContainer = $('#chatContainer');
                const chatMessage = $('<div>').addClass('chat-message my-chat');
                chatMessage.html(`
                    <div class="chat-bubble-container my-chat">
                        <img src="${e.target.result}" class="chat-image">
                        <div class="timeAndRead my-chat">
                            <div class="unread">2</div>
                            <div class="time">오후 ${new Date().getHours()}:${new Date().getMinutes()}</div>
                        </div>
                    </div>
                `);
                chatContainer.append(chatMessage);
                chatContainer.scrollTop(chatContainer[0].scrollHeight);
            }
            reader.readAsDataURL(file);
        }
    }

    // 채팅창에 올라온 이미지 클릭하면 전체 화면으로 표시
    function openFullscreen(src) {
        $('#fullscreenImage').attr('src', src);
        $('#fullscreenOverlay').css('display', 'flex');
    }

    // 전체 화면 이미지 닫기
    function closeFullscreen() {
        $('#fullscreenOverlay').css('display', 'none');
        $('#fullscreenImage').attr('src', '');
    }

    // 사진(첨부)버튼 클릭 시 파일 선택창 오픈
    $('.photo-button').on('click', function() {
        $('#fileInput').click();
    });

    // 파일 선택 시 선택한 파일 미리보기 표시
    $('#fileInput').on('change', previewImage);
    // 전송버튼 클릭 > 메세지 전송
    $('.send-button').on('click', sendMessage);
    // 텍스트 입력 시 입력창 높이 조절
    $('.chat-input').on('input', function () {
        resizeInput(this);
    });

    // 동적으로 추가된 .chat-image 이미지에 이벤트처리 (이미지 전체화면 표시)
    $(document).on('click', '.chat-image', function () {
        openFullscreen($(this).attr('src'));
    });

    $('.close-button').on('click', closeFullscreen);
});
