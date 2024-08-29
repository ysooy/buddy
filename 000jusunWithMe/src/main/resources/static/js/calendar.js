document.addEventListener('DOMContentLoaded', function() {
    var groupNoElement = document.getElementById('groupNo');

    // groupNoElement가 null이 아닌지 확인
    if (groupNoElement) {
        var groupNo = groupNoElement.value;
    } else {
        return; // groupNo를 찾지 못하면 이후 코드를 실행X
    }

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
        events: [], // 초기 이벤트 배열 (ajax를 통해 로드 예정)

        // 날짜 클릭했을 시 호출되는 함수 (일정 등록)
        dateClick: function(scheduleInfo) {
            var dateStr = scheduleInfo.dateStr; // 클릭한 날짜 가져오기
            $("#startDate").val(dateStr); // 시작 날짜 입력필드에 클릭한 날짜 설정
            $("#endDate").val(dateStr); // 종료 날짜 입력 필드에 클릭한 날짜 설정
            $("#schedule").val(''); // 일정 내용 필드 비우기
            $(".modalSubmit").text('등록').removeClass('updateEvent').addClass('addEvent'); // 일정 등록이므로 '수정'이 아닌 '등록'버튼 
            $(".modalDelete").hide(); // 등록 모달에서는 삭제 버튼 숨기기
            $("#myModal").css("display", "flex"); // 모달창 띄우기 
            // 컬러팔레트 선택 상태 초기화 
            $('.colorCircle').removeClass('selected');
            selectedColor = null;
        },

        // 일정 이벤트 클릭 시 호출되는 함수 (일정 수정) 
        eventClick: function(scheduleInfo) {
            registeredEvent = scheduleInfo.event; // 클릭한 이벤트 불러오기
            var startDate = registeredEvent.start;
            var endDate = registeredEvent.end ? new Date(registeredEvent.end.getTime() - (1000 * 60 * 60 * 24)) : registeredEvent.start; // 종료 날짜가 없으면 시작 날짜로 설정
            selectedColor = registeredEvent.backgroundColor; // 기존 색상 저장

            // 날짜 형식을 "yyyy-mm-dd"로 변환 (수정 시 기존의 시작날짜가 달라지는 현상 해결)
            function formatDate(date) {
                var dateChange = new Date(date),
                    year = dateChange.getFullYear(),
                    month = (dateChange.getMonth() + 1).toString().padStart(2, '0'), // 월이 한 자리 수면 앞에 0 추가
                    day = dateChange.getDate().toString().padStart(2, '0'); // 일이 한 자리 수면 앞에 0 추가

                return `${year}-${month}-${day}`;
            }

            $("#startDate").val(formatDate(startDate)); // 등록된 이벤트의 시작 날짜 설정
            $("#endDate").val(formatDate(endDate)); // 등록된 이벤트 종료 날짜 설정
            $("#schedule").val(registeredEvent.title); // 등록된 일정 내용으로 설정

            // 기존 색상을 선택된 상태로 표시
            $('.colorCircle').removeClass('selected'); // 초기화
            $('.colorCircle').each(function() {
                if (rgbTohex($(this).css('background-color')) === selectedColor) {
                    $(this).addClass('selected');
                }
            });

            $(".modalSubmit").text('수정').removeClass('addEvent').addClass('updateEvent'); // 일정 수정이므로 '등록'이 아닌 '수정'버튼
            $(".modalDelete").show(); // 수정 모달에서는 삭제 버튼 보이기
            $("#myModal").css("display", "flex"); // 모달창 띄우기
        },

        // 이벤트 드래그 앤 드롭 후 호출되는 함수
        eventDrop: function(updateInfo) {
            var updatedEvent = updateInfo.event;
            var newStartDate = new Date(updatedEvent.start.getTime()); // updatedEvent.start객체 직접 수정하지 않고 복사해서 수정(원본객체를 사용하는 다른 코드에 영향을 미치지 않기 위함)
            var newEndDate = updatedEvent.end ? new Date(updatedEvent.end.getTime()) : new Date(updatedEvent.start.getTime() + (1000 * 60 * 60 * 24));

            // startDate에 1일 추가(db에 하루 전날로 설정될 수 있어서 이를 방지)
            newStartDate.setDate(newStartDate.getDate() + 1);
            // endDate에 1일 추가
            newEndDate.setDate(newEndDate.getDate() + 1);

            // 서버로 수정된 일정 정보 전송
            $.ajax({
                url: '/calendar/update/' + groupNo,
                type: 'POST',
                data: {
                    calendarNo: updatedEvent.extendedProps.calendarNo, // 수정할 이벤트 calendarNo
                    cContent: updatedEvent.title,
                    startDate: newStartDate.toISOString().slice(0, 10), // 시작 날짜에 1일 추가
                    endDate: newEndDate.toISOString().slice(0, 10), // 종료 날짜에 1일 추가
                    selectedColor: updatedEvent.backgroundColor, // 선택된 색상 또는 기존 색상
                    groupNo: groupNo
                },
                success: function(response) { // 요청 성공시 실행
                    alert(response);
                },
                error: function(xhr, status, error) {
                    console.error("xhr: ", xhr); // 응답객체(요청에 대한 정보)
                    console.error("Status: ", status); // 요청 실패 이유
                    console.error("Error: ", error); // 발생한 오류 설명
                    alert("오류가 발생했습니다." + error);
                }
            });
        }

    });


    // DB에서 이벤트 데이터를 불러와서 캘린더에 추가
    $.ajax({
        url: '/calendar/' + groupNo + '/schedules',
        type: 'GET',
        success: function(data) {
            data.forEach(function(event) {
                calendar.addEvent({
                    title: event.title,
                    start: event.start,
                    end: event.end,
                    allDay: true,
                    color: event.color,
                    extendedProps: {
                        calendarNo: event.calendarNo
                    }
                });
            });
            calendar.render();
        },
        error: function(xhr, status, error) {
            console.error("xhr: ", xhr); // 응답객체(요청에 대한 정보)
            console.error("Status: ", status); // 요청 실패 이유
            console.error("Error: ", error); // 발생한 오류 설명
            alert("오류가 발생했습니다." + error);
        }
    });

    //calendar.render();

    // 모달창 일정 등록 버튼 클릭 시
    $(document).on('click', '.addEvent', function() {
        var cContent = $("#schedule").val(); // 일정 내용
        var startDate = $("#startDate").val(); // 시작 날짜
        var endDate = $("#endDate").val(); // 종료 날짜
        var selectedColor = $('.colorCircle.selected').css('background-color'); // 색상 선택

        // 색상이 선택되지 않았을 경우
        if (!selectedColor) {
            alert("일정, 날짜 및 색상을 모두 입력해주세요.");
            return;
        }


        // RGB 색상 값을 hex 값으로 변환하는 함수 호출
        selectedColor = rgbTohex(selectedColor);

        // 모든 필드가 채워져 있는지 확인
        if (cContent && startDate && endDate && selectedColor) {
            $.ajax({
                url: '/calendar/add', // 서버의 /calendar/add 엔드포인트로 요청
                type: 'POST',
                data: {
                    cContent: cContent,
                    startDate: startDate,
                    endDate: new Date(new Date(endDate).getTime() + (1000 * 60 * 60 * 24)).toISOString().slice(0, 10), // 종료 날짜 +1로 저장
                    selectedColor: selectedColor,
                    groupNo: groupNo
                },
                success: function(response) { // 요청 성공시 실행
                    alert(response.message); // 응답 메시지
                    var newEvent = {
                        title: cContent,
                        start: startDate,
                        end: new Date(new Date(endDate).getTime() + (1000 * 60 * 60 * 24)).toISOString().slice(0, 10), // 종료 날짜 +1로 설정
                        allDay: true,
                        color: selectedColor,
                        extendedProps: {
                            calendarNo: response.calendarNo // 서버에서 반환된 calendarNo 사용
                        }
                    };
                    calendar.addEvent(newEvent);
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

    // 모달창에서 '삭제'버튼 클릭 시 일정 삭제
    $(document).on('click', '.deleteEvent', function() {
        if (registeredEvent) {
            if (confirm('정말 삭제하시겠습니까?')) {
                // 서버로 삭제 요청
                $.ajax({
                    url: '/calendar/delete/' + groupNo + '/' + registeredEvent.extendedProps.calendarNo,
                    type: 'GET',
                    success: function(response) { // 요청 성공시 실행
                        alert(response);
                        // 캘린더에서 이벤트 삭제
                        registeredEvent.remove();

                        // 모달창 닫기
                        $("#myModal").css("display", "none");
                    },
                    error: function(xhr, status, error) {
                        console.error("xhr: ", xhr); // 응답객체(요청에 대한 정보)
                        console.error("Status: ", status); // 요청 실패 이유
                        console.error("Error: ", error); // 발생한 오류 설명
                        alert("오류가 발생했습니다." + error);
                    }
                });
            }
        } else {
            alert("삭제할 이벤트가 없습니다.");
        }
    });


    // 모달창에서 '수정'버튼 클릭 시 일정 업데이트 (등록되어있는 이벤트 클릭)
    $(document).on('click', '.updateEvent', function() {
        var cContent = $("#schedule").val();
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();
        var newSelectedColor = $('.colorCircle.selected').css('background-color'); // 선택된 색상 가져오기

        // 필드가 전부 채워져 있고 registeredEvent(이미 등록되어 있는 이벤트)가 존재하는 경우에만 실행
        if (cContent && startDate && endDate && registeredEvent) {
            // 선택된 색상이 없으면 기존 색상 사용 (수정 시 색상 선택을 안해도 수정 가능)
            if (!newSelectedColor) {
                newSelectedColor = selectedColor;
            } else {
                // 선택된 색상이 있을 경우, hex로 변환
                newSelectedColor = rgbTohex(newSelectedColor);
            }

            // 종료 날짜를 그대로 사용 (DB에는 실제 종료 날짜 저장)
            var start = new Date(startDate);
            var end = new Date(endDate); // 종료 날짜 그대로

            // 서버로 수정된 일정 정보 전송
            $.ajax({
                url: '/calendar/update/' + groupNo,
                type: 'POST',
                data: {
                    calendarNo: registeredEvent.extendedProps.calendarNo, // 수정할 이벤트 calendarNo
                    cContent: cContent,
                    startDate: startDate,
                    endDate: new Date(new Date(endDate).getTime() + (1000 * 60 * 60 * 24)).toISOString().slice(0, 10), // 종료 날짜 +1로 저장
                    selectedColor: newSelectedColor, // 선택된 색상 또는 기존 색상
                    groupNo: groupNo
                },
                success: function(response) { // 요청 성공시 실행
                    alert(response);
                    
                    // 캘린더의 일정 정보 업데이트
                    registeredEvent.setProp('title', cContent);
                    registeredEvent.setStart(startDate);
                    // 표시되는 날짜와 DB간의 일관성 유지, 정확한 종료 날짜 반영.
                    registeredEvent.setEnd(new Date(new Date(endDate).getTime() + (1000 * 60 * 60 * 24)).toISOString().slice(0, 10)); // 종료 날짜 +1로 설정
                    registeredEvent.setProp('backgroundColor', newSelectedColor);
                    registeredEvent.setProp('borderColor', newSelectedColor);

                    // 모달창 닫기
                    $("#myModal").css("display", "none");

                    // 모달창 입력필드 초기화
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
    function rgbTohex(rgb) {
        rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/); // RGB값에서 숫자만 추출
        function hex(x) {
            return ("0" + parseInt(x).toString(16)).slice(-2); // 각 색상값을 16진수로 변환
        }
        return "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]); // hex색상값 반환
    }


    // 일정 등록 모달창 설정
    $(document).on('click', '#openModal', function() {
        var today = new Date().toISOString().slice(0, 10);
        $("#startDate").val(today);
        $("#endDate").val(today);
        $("#schedule").val('');
        $(".modalSubmit").text('등록').removeClass('updateEvent').addClass('addEvent');
        $(".modalDelete").hide(); // 등록 모달에서는 삭제 버튼 숨기기
        $("#myModal").css("display", "flex");
        // 컬러팔레트 선택 상태 초기화
        $('.colorCircle').removeClass('selected');
        selectedColor = null;
    });

    // 모달창 닫기
    $(document).on('click', '.closeModal', function() {
        $("#myModal").css("display", "none");
        // 컬러 선택 상태 초기화
        $('.colorCircle').removeClass('selected');
        selectedColor = null;
    });

    // 색상 선택
    $(document).on('click', '.colorCircle', function() {
        $('.colorCircle').removeClass('selected');
        $(this).addClass('selected');
        selectedColor = $(this).css('background-color');
        // 색상 선택 시(일정이 이틀 이상) rgb로 변하는 것 다시 변환
        selectedColor = rgbTohex(selectedColor);
    });

    // 날짜 선택창(달력에서 날짜 선택하면 날짜가 입력됨)
    $("#startDate, #endDate").datepicker({
        dateFormat: "yy-mm-dd", // 원하는 날짜 형식
        onSelect: function(dateText) {
            $(this).val(dateText);
        }
    });
});