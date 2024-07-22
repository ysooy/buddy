document.addEventListener('DOMContentLoaded', function() {
    // 네비게이션 바 로드
    $("#navbar").load("/navbar/navbar.html");
    // 헤더 로드 (뒤로 가기 버튼 숨김처리)
    $("#header").load("/header/header.html", function() {
        $(".leftButton").css("visibility", "hidden");
        // myPage 아이콘을 + 버튼으로 변경
        $(".myPage").html('<a href="#" id="openModal" style="font-size: 40px; text-decoration: none;">+</a>');
    });

    // 선택한 색상 변수
    var selectedColor = null;
    // 등록되어 있는 이벤트(초기에는 없으니 null)
    var registeredEvent = null;

    // Color circles 생성
    const colors = ["#FF8982", "#FFBF82", "#FFFF82", "#82FFB3", "#82C4FF", "#8B5BE7", "#FFA783", "#82E8D4", "#FFB99C", "#B5E2CB"];
    const colorPalette = document.querySelector('.colorPalette');

    colors.forEach(color => {
        const colorCircle = document.createElement('div');
        colorCircle.className = 'colorCircle';
        colorCircle.style.backgroundColor = color;
        colorPalette.appendChild(colorCircle);
    });


    // 일정 등록 모달창 설정
    $(document).on('click', '#openModal', function() {
        var today = new Date().toISOString().slice(0, 10);
        $("#startDate").val(today);
        $("#endDate").val(today);
        $("#schedule").val('');
        $(".modalSubmit").text('등록').removeClass('updateEvent').addClass('addEvent');
        $("#myModal").css("display", "flex");
        // 컬러팔레트 선택 상태 초기화
        $('.colorCircle').removeClass('selected');
        selectedColor = null;
    });

    // 모달창 닫기
    $(document).on('click', '.closeModal', function() {
        $("#myModal").css("display", "none");
    });

    // 색상 선택
    $(document).on('click', '.colorCircle', function() {
        $('.colorCircle').removeClass('selected');
        $(this).addClass('selected');
        selectedColor = $(this).css('background-color');
    });

    // 날짜 선택창(달력에서 날짜 선택하면 날짜가 입력됨)
    $("#startDate, #endDate").datepicker({
        dateFormat: "yy-mm-dd", // 원하는 날짜 형식
        onSelect: function(dateText) {
            $(this).val(dateText);
        }
    });

    // 달력 설정
    var calendarEl = document.getElementById('calendar');
    // FullCalendar 달력 생성
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth', // 초기 뷰를 월간으로
        locale: 'ko', // 한국어로 표시
        headerToolbar: { // 헤더 툴바 설정
            left: 'prev',
            center: 'title',
            right: 'next'
        },
        editable: true, // 달력 이벤트를 드래그 앤 드롭으로 편집
        selectable: true, // 달력에서 날짜 선택 가능
        events: [], // 초기 이벤트 배열	(ajax를 통해 로드 예정)

        // 날짜 클릭했을 시 호출되는 함수 (일정 등록)
        dateClick: function(info) {
            var dateStr = info.dateStr; // 클릭한 날짜 가져오기
            $("#startDate").val(dateStr); // 시작 날짜 입력필드에 클릭한 날짜 설정
            $("#endDate").val(dateStr); // 종료 날짜 입력 필드에 클릭한 날짜 설정
            $("#schedule").val(''); // 일정 내용 필드 비우기
            $(".modalSubmit").text('등록').removeClass('updateEvent').addClass('addEvent'); // 일정 등록이므로 '수정'이 아닌 '등록'버튼
            $("#myModal").css("display", "flex"); // 모달창 띄우기
            // 컬러팔레트 선택 상태 초기화
            $('.colorCircle').removeClass('selected'); // 추가된 코드
            selectedColor = null; // 추가된 코드
        },

        // 일정 이벤트 클릭 시 호출되는 함수 (일정 수정)
        eventClick: function(info) {
            registeredEvent = info.event; // 클릭한 이벤트 불러오기
            $("#startDate").val(registeredEvent.start.toISOString().slice(0, 10)); // 등록된 이벤트의 시작 날짜 설정
            $("#endDate").val(registeredEvent.end ? registeredEvent.end.toISOString().slice(0, 10) : registeredEvent.start.toISOString().slice(0, 10)); //등록된 이벤트 종료 날짜 설정
            $("#schedule").val(registeredEvent.title); // 등록된 일정 내용으로 설정
            $(".modalSubmit").text('수정').removeClass('addEvent').addClass('updateEvent'); // 일정 수정이므로 '등록'이 아닌 '수정'버튼
            $("#myModal").css("display", "flex"); // 모달창 띄우기
        }
    });

    // DB에서 이벤트 데이터를 불러와서 캘린더에 추가
    $.ajax({
        url: '/calendar/events',
        type: 'GET',
        success: function(response) {
            response.forEach(function(event) {
                calendar.addEvent({
                    title: event.title,
                    start: event.start,
                    end: event.end,
                    allDay: true,
                    color: event.color
                });
            });
        },

        error: function(xhr, status, error) {
            console.error("xhr: ", xhr); // 응답객체(요청에 대한 정보)
            console.error("Status: ", status); // 요청 실패 이유
            console.error("Error: ", error); // 발생한 오류 설명
            alert("오류가 발생했습니다." + error);

        }

    });

    calendar.render();

    // 모달창 일정 등록 버튼 클릭 시
    $(document).on('click', '.addEvent', function() {
        var eventContent = $("#schedule").val(); // 일정 내용
        var startDate = $("#startDate").val(); // 시작 날짜
        var endDate = $("#endDate").val(); // 종료 날짜
        var selectedColor = $('.colorCircle.selected').css('background-color'); // 색상 선택


        // RGB 색상 값을 hex 값으로 변환하는 함수 호출
        selectedColor = rgb2hex(selectedColor);

        // 모든 필드가 채워져 있는지 확인
        if (eventContent && startDate && endDate && selectedColor) {
            $.ajax({
                url: '/calendar/add', // 서버의 /calendar/add 엔드포인트로 요청
                type: 'POST',
                data: {
                    eventContent: eventContent,
                    startDate: startDate,
                    endDate: endDate,
                    selectedColor: selectedColor
                },
                success: function(response) { // 요청 성공시 실행
                    alert(response);
                    calendar.addEvent({
                        title: eventContent,
                        start: startDate,
                        // 이벤트 종료 날짜+1로 설정하여 종료 날짜까지 포함시켜야 내가 원하는 종료 날짜 설정 가능
                        end: endDate ? new Date(new Date(endDate).setDate(new Date(endDate).getDate() + 1)).toISOString().slice(0, 10) : null,
                        allDay: true,
                        color: selectedColor
                    });
                    // 모달창 닫고 입력 필드 초기화.
                    $("#myModal").css("display", "none");
                    $("#schedule").val("");
                    $("#startDate").val("");
                    $("#endDate").val("");
                    selectedColor = '';
                },
                error: function(xhr, status, error) {
                    console.error("xhr: ", xhr); // 응답객체(요청에 대한 정보)
                    console.error("Status: ", status); // 요청 실패 이유
                    console.error("Error: ", error); // 발생한 오류 설명
                    alert("오류가 발생했습니다." + error);
                }
            });
        } else {
            alert("일정, 날짜 및 색상을 모두 입력해주세요.");
        }
    });

    // RGB색상값을 hex값으로 변환하는 함수
    function rgb2hex(rgb) {
        rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/); // RGB값에서 숫자만 추출
        function hex(x) {
            return ("0" + parseInt(x).toString(16)).slice(-2); // 각 색상값을 16진수로 변환
        }
        return "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]); // hex색상값 반환
    }


    // 모달창에서 '수정'버튼 클릭 시 일정 업데이트 (등록되어있는 이벤트 클릭)
    $(document).on('click', '.updateEvent', function() {
        var eventContent = $("#schedule").val();
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();

        // 필드가 전부 채워져 있고 registeredEvent(이미 등록되어 있는 이벤트)가 존재하는 경우에만 실행
        if (eventContent && startDate && endDate && selectedColor && registeredEvent) {
            var start = new Date(startDate);
            var end = new Date(endDate);
            end.setDate(end.getDate() + 1); // 종료날짜를 포함하기 위해서 +1 추가

            // 일정, 시작날짜, 종료날짜, 이벤트 색상 수정
            registeredEvent.setProp('title', eventContent);
            registeredEvent.setStart(startDate);
            // 종료 날짜가 시작 날짜보다 이후인 경우 : 종료 날짜를 다음날로 설정
            if (start <= end) {
                registeredEvent.setEnd(end.toISOString().slice(0, 10));
            } else {
                registeredEvent.setEnd(endDate);
            }

            registeredEvent.setProp('backgroundColor', selectedColor);
            registeredEvent.setProp('borderColor', selectedColor);

            // 모달창 닫기
            $("#myModal").css("display", "none");

            // 모달창 입력필드 초기화
            $("#schedule").val("");
            $("#startDate").val("");
            $("#endDate").val("");
            selectedColor = '';
        } else {
            alert("일정, 날짜 및 색상을 모두 입력해주세요.");
        }
    });
});