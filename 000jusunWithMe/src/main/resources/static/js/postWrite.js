$(function () {
    // 헤더 그룹명으로
    $(".center h2").text("그룹명");
    
    // #date에 현재 날짜 넣기
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth() + 1;
    const day = today.getDate();
    const todayDate = `${year}년 ${month}월 ${day}일`;
    $('#date').text(todayDate);
    
    // 사진 입력 처리(압축), 미리보기
    $('#photoUpload').on('change', function() {
        const files = this.files;
        const previewContainer = $('#imagePreview');
        previewContainer.empty();

        const dataTransfer = new DataTransfer();

        Array.from(files).forEach(file => {
            if (file.type.startsWith('image/')) {
                if (file.type === 'image/png') { // PNG 파일은 미리보기만 처리
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        const imgElement = $('<img>').attr('src', e.target.result).css({ width: '150px', height: '150px', margin: '10px' });
                        previewContainer.append(imgElement);
                    };
                    reader.readAsDataURL(file);
                } else if (file.type === 'image/heic') { // HEIC 파일 처리 -> JPEG로 
                    heic2any({blob: file, toType: 'image/jpeg'})
                        .then(function(jpegBlob) {
                            processImage(jpegBlob, file);
                        })
                        .catch(function(error) {
                            console.error('HEIC 변환 오류:', error);
                        });
                } else {  // 기타 이미지 (예: JPEG) 처리
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        const img = new Image();
                        img.src = e.target.result;
                        img.onload = function() {
                            const canvas = document.createElement('canvas');
                            const ctx = canvas.getContext('2d');

                            // 이미지 크기 조정
                            const MAX_WIDTH = 800;
                            const MAX_HEIGHT = 600;
                            let width = img.width;
                            let height = img.height;

                            if (width > height) {
                                if (width > MAX_WIDTH) {
                                    height *= MAX_WIDTH / width;
                                    width = MAX_WIDTH;
                                }
                            } else {
                                if (height > MAX_HEIGHT) {
                                    width *= MAX_HEIGHT / height;
                                    height = MAX_HEIGHT;
                                }
                            }

                            canvas.width = width;
                            canvas.height = height;
                            ctx.drawImage(img, 0, 0, width, height);

                            // EXIF 데이터 추출 (JPEG 파일 등)
                            const originalExif = piexif.load(e.target.result);
                            canvas.toBlob(function(blob) {
                                processImage(blob, file, originalExif);
                            }, 'image/jpeg', 0.85); // 압축 품질 0.85
                        };
                    };
                    reader.readAsDataURL(file);
                }
            }
        });

        function processImage(blob, originalFile, originalExif) {
            const reader = new FileReader();
            reader.onloadend = function() {
                let compressedImageWithExif = blob;
                if (originalExif) {
                    // EXIF 데이터 삽입
                    compressedImageWithExif = piexif.insert(piexif.dump(originalExif), reader.result);
                    compressedImageWithExif = dataURLtoBlob(compressedImageWithExif);
                }
                const uniqueFileName = generateUniqueFileName(originalFile.name);
                const compressedFile = new File([compressedImageWithExif], uniqueFileName, { type: 'image/jpeg', lastModified: Date.now() });

                // 압축된 이미지 미리보기 추가
                const imgElement = $('<img>').attr('src', URL.createObjectURL(compressedFile)).css({ width: '150px', height: '150px', margin: '10px' });
                previewContainer.append(imgElement);

                // 압축된 파일을 DataTransfer에 추가
                dataTransfer.items.add(compressedFile);

                // 압축된 파일로 input 파일을 대체
                $('#photoUpload')[0].files = dataTransfer.files;
            };
            reader.readAsDataURL(blob);
        }
		
		//유일 파일명 만들기
        function generateUniqueFileName(originalName) {
            // 1. 현재 시간을 밀리초로 가져옴
            const timestamp = Date.now();
            
            // 2. 원본 파일명 추출
            const originalNameWithoutExtension = originalName.split('.').slice(0, -1).join('.');
            
            // 3. 파일 확장자 추출
            const extension = originalName.split('.').pop();
            
            // 1+2+3 고유한 파일명을 생성해 반환
            return `${timestamp}_${originalNameWithoutExtension}.${extension}`;
        }
		
		//DataUrl을 Blob으로 변환 (Blob: Binary Large Object)
        function dataURLtoBlob(dataurl) {
            const arr = dataurl.split(',');
            const mime = arr[0].match(/:(.*?);/)[1];
            const bstr = atob(arr[1]);
            let n = bstr.length;
            const u8arr = new Uint8Array(n);
            while (n--) {
                u8arr[n] = bstr.charCodeAt(n);
            }
            return new Blob([u8arr], { type: mime });
        }
    });
});
