
// 스크롤을 채팅창의 최하단으로 이동시키는 함수
function scrollToBottom() {
    const chatContainer = $('.chat-container');
    chatContainer.scrollTop(chatContainer[0].scrollHeight);
}

// 텍스트 입력 시 높이 자동 조절 (최대 100px)
function resizeInput(textarea) {
    textarea.style.height = 'auto';
    textarea.style.height = (textarea.scrollHeight > 100 ? 100 : textarea.scrollHeight) + 'px';
}


// 채팅 날짜 표시
function isNewDate(message, lastMessage) {
    if (!lastMessage) return true;
    const lastMessageDate = new Date(lastMessage.sentTime);
    const currentMessageDate = new Date(message.sentTime);
    return lastMessageDate.toDateString() !== currentMessageDate.toDateString();
}


$(document).ready(function() {
    // 페이지 로드 후 약간 지연 이후 스크롤을 최하단으로 이동(페이지 로드 시 스크롤이 가장 아래로 위치하도록)
    setTimeout(function() {
        scrollToBottom();
    }, 100);

    var lastMessage = null; // 마지막 메세지 저장용(날짜 표시 위함)
    var lastDateSeparator = null; // 마지막 date-separator의 날짜 저장


    // 페이지 로드 시 가장 마지막 메시지를 lastMessage로 설정
    const loadedMessages = $('.chat-container .chat-message');
    if (loadedMessages.length > 0) {
        const lastLoadedMessage = loadedMessages.last().data('message');
        lastMessage = lastLoadedMessage;

        // 마지막 date-separator의 날짜를 저장
        const lastSeparator = $('.chat-container .date-separator-container').last();
        if (lastSeparator.length > 0) {
            lastDateSeparator = lastSeparator.text().trim();
        }
    }

    var socket = new SockJS('/websocket');
    var stompClient = Stomp.over(socket);
    // 웹소켓 연결 함수
    function connect() {
        stompClient.connect({}, function(frame) {
            console.log('웹소켓에 연결됨');
            stompClient.subscribe('/topic/messages', function(messageOutput) {
                console.log('수신한 메세지 : ', messageOutput.body);		
                showMessage(JSON.parse(messageOutput.body));
            });
        }, function(error) {
            console.log('웹소켓 연결 실패:', error);
        });
    }

    // 웹소켓 연결 시도
    connect();

    // 웹소켓 연결 종료 시 재연결 시도
    stompClient.onclose = function() {
        setTimeout(function() {
            connect();  // 웹소켓 재연결 시도
        }, 5000);  // 5초 후에 재연결 시도
    };
    
    
    
    var originalInputContainerHtml = $('.chat-input-container').html();


    // 현재 선택된 검색 결과 인덱스 (유효한 인덱스는 0부터 시작, -1 : 검색결과 아직 선택 안된 상황)
    var currentSearchIndex = -1;
    var searchResults = [];

   
    
    // 입력한 메세지 전송버튼 클릭 시 서버로 전송
    function sendMessage() {
        const input = $('.chat-input');
        const message = input.val();
        if (message.trim() !== '') {
            var chatMessage = {
                sentTime: new Date().toISOString(), // 시간을 ISO 형식으로 전송
                content: message,
                msgFname: '',
                msgType: 1,
                userNo: userNo,
                groupNo: groupNo,
                imgBase64: ''
            };
            stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));

            // 클라이언트에서 화면에 추가하는 부분 제거 (서버 응답 후 화면에 추가)
            input.val('');
            input.css('height', 'auto');
        }
    }



    // 이미지 메시지 전송
    function sendImageMessage(fileName) {
        var chatMessage = {
            sentTime: new Date(),
            content: '',
            msgFname: fileName,
            msgType: 2, // 이미지 타입
            userNo: userNo,
            groupNo: groupNo,
            imgBase64: ''
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
    }


    // 수신한 메시지 채팅창에 표시
    function showMessage(message) {
		
        // 현재 로그인한 userNo 변수 참조
        const currentUserNo = userNo;
		
		console.log("message.userNo:", message.userNo, "currentUserNo:", currentUserNo);

	    console.log('실시간 메세지 표시:', message);		
        const chatContainer = $('.chat-container');

        // 새 메시지의 날짜를 yyyy년 M월 d일 형식으로
        const messageDate = new Date(message.sentTime).toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

        // 현재 메시지와 마지막 date-separator의 날짜 비교
        const lastDateSeparatorText = chatContainer.find('.date-separator').last().text().trim();

        // 메시지 추가 전 마지막 date-separator와 새 메시지 날짜 비교
        if (lastDateSeparatorText !== messageDate && lastDateSeparator !== messageDate) {
            // 같은 날짜의 구분선이 없다면 추가
            const dateSeparator = $('<div>').addClass('date-separator-container').html(
                '<div class="date-separator">' + messageDate + '</div>'
            );
            chatContainer.append(dateSeparator);

            // 마지막 date-separator 업데이트
            lastDateSeparator = messageDate;
        }
        
        
        // 메시지를 화면에 추가하는 코드
        const isMyMessage = parseInt(message.userNo, 10) === parseInt(currentUserNo, 10);
        const chatMessage = $('<div>').addClass('chat-message').addClass(isMyMessage ? 'my-chat' : 'friend-chat');

        if (message.msgType === 2) { // 이미지 타입
            chatMessage.html(
                '<div class="chat-bubble-container ' + (isMyMessage ? 'my-chat' : 'friend-chat') + '">' +
                '<img src="' + message.msgFname + '" class="chat-image">' +
                '<div class="timeAndRead ' + (isMyMessage ? 'my-chat' : 'friend-chat') + '">' +
                //'<div class="unread">2</div>' +
            	(message.unread > 0 ? '<div class="unread">' + message.unread + '</div>' : '') + // unread가 0보다 큰 경우에만 표시
                '<div class="time">' + new Date(message.sentTime).toLocaleTimeString('ko-KR', {
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: true
                }) + '</div>' +
                '</div>' +
                '</div>'
            );
        } else {
            chatMessage.html(
                '<div class="chat-bubble-container ' + (isMyMessage ? 'my-chat' : 'friend-chat') + '">' +
                '<div class="chat-bubble ' + (isMyMessage ? 'my-chat' : 'friend-chat') + '">' +
                message.content.replace(/\n/g, '<br>') + '</div>' +
                '<div class="timeAndRead ' + (isMyMessage ? 'my-chat' : 'friend-chat') + '">' +
                //'<div class="unread">2</div>' +
	            (message.unread > 0 ? '<div class="unread">' + message.unread + '</div>' : '') + // unread가 0보다 큰 경우에만 표시              
                '<div class="time">' + new Date(message.sentTime).toLocaleTimeString('ko-KR', {
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: true
                }) + '</div>' +
                '</div>' +
                '</div>'
            );
        }

        chatContainer.append(chatMessage);

        // 마지막 메시지 업데이트
        lastMessage = message;
        
        // 메시지 추가 후 스크롤을 최하단으로 이동
        setTimeout(function() {
            scrollToBottom();
        }, 100); // 약간의 지연을 주니 더 빠르게 출력됨
    }


    // 스크롤을 채팅창의 최하단으로 이동시키는 함수
    //    function scrollToBottom() {
    //        const chatContainer = $('.chat-container');
    //        chatContainer.scrollTop(chatContainer[0].scrollHeight);
    //    }

    // 페이지 로드 후 스크롤을 최하단으로 이동
    //    scrollToBottom();



    // 이미지 파일 선택 시 서버에 업로드
    function uploadImage(file) {
        const formData = new FormData();
        formData.append('file', file);

        $.ajax({
            url: '/uploadImage',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                // 업로드 성공 후 이미지 메시지 전송
                sendImageMessage(response.fileName);
            },
            error: function() {
                console.error('이미지 업로드에 실패했습니다.');
            }
        });
    }

    // 파일 선택 시 이미지 업로드
    function previewImage(event) {
        const file = event.target.files[0];
        if (file) {
            uploadImage(file);
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
    $('.chat-input').on('input', function() {
        resizeInput(this);
    });

    // 동적으로 추가된 .chat-image 이미지에 이벤트처리 (이미지 전체화면 표시)
    $(document).on('click', '.chat-image', function() {
        openFullscreen($(this).attr('src'));
    });

    $('.close-button').on('click', closeFullscreen);

    // 검색 input 클릭 이벤트 (input창을 클릭하면 바로 검색모드 UI로 변경)
    $('.search-input').on('focus', function() {
        switchToSearchNav();
    });

    // 검색 버튼 클릭 이벤트
    $('.search-button').on('click', function() {
        // 검색어 입력(비어있으면 함수 종료)
        const searchTerm = $('.search-input').val().trim();
        if (searchTerm === '') return;

        // 이전 검색 결과가 있다면 초기화, 이전 검색 하이라이트 삭제
        $('.chat-bubble').removeClass('highlight'); // 이전 하이라이트 제거

        // 이전 검색 결과 초기화하기
        searchResults = [];
        // 현재 검색 인덱스 초기화하기
        currentSearchIndex = -1;

        // 채팅 메세지에서 검색어 포함하는 요소 찾기 (역순(제일 아래부터 찾기))
        // 검색된 결과가 최신~오래된 메세지 순서로 배열됨 (인덱스 0 : 제일 최신메세지, 커지면 오래된 메세지)
        $($('.chat-bubble').get().reverse()).each(function(index) { // 역순으로 검색
            if ($(this).text().includes(searchTerm)) {
                searchResults.push($(this)); //검색결과 배열에 추가하기
            }
        });
        // 검색어가 있으면 맨 처음 결과 표시
        if (searchResults.length > 0) {
            highlightSearchResult(0);
        }
    });

    // 검색 결과 표시하고 스크롤 이동하기
    function highlightSearchResult(index) {
        if (index < 0 || index >= searchResults.length) return;
        if (currentSearchIndex !== -1) {
            searchResults[currentSearchIndex].removeClass('highlight'); // 이전 하이라이트 제거
        }
        currentSearchIndex = index;
        const element = searchResults[currentSearchIndex];
        element.addClass('highlight'); // 현재 검색 결과 하이라이트
        $('.chat-container').scrollTop(element.offset().top - $('.chat-container').offset().top + $('.chat-container').scrollTop());
    }

    // 문자 검색시 채팅 입력창을 검색모드로 바꾸기
    function switchToSearchNav() {
        // '<'버튼 : 검색모드 취소, 위 화살표 : 문자검색 위로(이전 메세지), 아래 화살표 : 문자검색 아래로(최근 메세지)
        $('.chat-input-container').html(
            '<button class="cancel-button">취소</button>' +
            '<div class="search-nav-buttons">' +
            '<button class="prev-button">↑</button>' +
            '<button class="next-button">↓</button>' +
            '</div>'
        );

        // 취소버튼 (원래 메세지 입력창으로 되돌리기, 배경색 원상복구)
        $('.cancel-button').on('click', function() {
            restoreInputContainer();
            $('.chat-bubble').removeClass('highlight'); // 하이라이트 제거
        });

        // searchResults가 역순으로 배열되어있음 (인덱스 증가하면 이전 검색결과로 이동)
        // 이전 메세지 찾기 버튼 클릭
        // 검색 결과 배열의 제일 끝에 도달하지 않았는지를 확인 > 인덱스 증가하여 이전 검색결과로 
        $('.prev-button').on('click', function() {
            if (currentSearchIndex < searchResults.length - 1) { // 검색 결과가 가장 오래된 메세지가 아니라면 아래 실행
                highlightSearchResult(currentSearchIndex + 1); // 이전 검색 결과로 이동하기
            }
        });
        // 다음 메세지 찾기 버튼 클릭
        // 검색 인덱스가 배열 시작(최신 메세지)인지 확인
        $('.next-button').on('click', function() {
            if (currentSearchIndex > 0) { // 검색 결과가 제일 최신 메세지가 아니라면 아래 실행(아래에 더 메세지들이 있으므로)
                highlightSearchResult(currentSearchIndex - 1); // 다음 검색 결과로 이동하기
            }
        });
    }

    // 검색 모드 취소 이후 기존 채팅 입력창 복원, 재설정
    // 메세지 검색 이후 채팅 메세지 전송 시 db에 연결되지 않는 문제 해결
    function restoreInputContainer() {
        $('.chat-input-container').html(originalInputContainerHtml);
        // 사진 버튼 클릭 시 파일 선택창 오픈
        $('.photo-button').on('click', function() {
            $('#fileInput').click();
        });
        // 파일 선택 시 선택한 파일 미리보기 표시
        $('#fileInput').on('change', previewImage);
        // 전송 버튼 클릭 시 메세지 전송
        $('.send-button').on('click', sendMessage);
        // 텍스트 입력 시 입력창 높이 조절
        $('.chat-input').on('input', function() {
            resizeInput(this);
        });
    }

    // 초기화(검색모드 종료하고 기존의 채팅 컨테이너로 복원)
    restoreInputContainer();
});