# BalbamBalbam - 외국인을 위한 한국어 발음 교정 시스템

![대표화면](https://github.com/Capstone-4Potato/.github/assets/108220648/61e54f7d-b165-4700-a1af-2b0d9d137cf0)

---
발밤발밤은 **한국어 발음 교정 모바일 서비스**로 빠르게 성장하는 한국어 시장 대비 한국어 교육 공급의 부족과 한국어 발음 학습의 어려움을 해소하기 위하여 제작되었습니다.

학습 카드의 표준 발음을 듣고, 따라하고, 구체적인 피드백을 받는 <듣기> <따라하기> <피드백 받기>의 3단계 학습을 할 수 있습니다.

또한 음절-단어-문장 커리큘럼,  발음 테스트, 사용자가 직접 학습카드 생성, 복습하기 등 더 다양한 서비스로 사용자의 효율적인 한국어 발음 학습을 돕습니다.

---
현재 [ios 앱스토어](https://apps.apple.com/kr/app/%EB%B0%9C%EB%B0%A4%EB%B0%9C%EB%B0%A4-balbambalbam/id6505030399)에서 다운로드 받으실 수 있습니다. 

시연 영상은 [해당 링크](https://www.youtube.com/watch?v=5z-CwNY1Nic)에서, 자세한 기능과 사용법은 [해당 링크](https://github.com/Capstone-4Potato/.github)를 참고 해주세요.

&nbsp;

## Backend Stack
**Language** : `Java 21`

**Framework** : `Spring 6.1` `SpringBoot 3.2` `SpringSecurity`

**OS** : `Linux CentOS 7` `bootJar 배포`

**DB & Data** : `MySQL 5.7` `JPA`

**ETC** : `Swagger` `OAuth` `JWT`

&nbsp;

## Update log
**| 2024.07.15 version 1.0.0 App 출시**


**| 2024.08.05 version 1.1.0 1차 update**
  - 한국어 발음 표기법 → 한영 뜻 제공으로 변경
  - 사진 base64로 인코딩해서 json으로 전송 → 서버에서 받아가는 것으로 변경(시간 단축)
  - 음절 학습 시 피드백 점수, 추천학습카드, STT 제공하지 않고 waveform과 녹음만 제공하도록 변경
  - 추천학습 카드 초성 → initial consonant, 중성 → Medial vowel, 종성 → final consonant로 변경


**|  2025.01.04 version 1.2.0 2차 update**
  - 홈 화면, 프로필, 피드백, 커스텀 카드. 알림창 UI & UX 개선
  - 새로운 기능 추가
    - 일일 학습 콘텐츠 제공 기능 
    - 오늘의 추천 학습 카드
    - 레벨 & 경험치 제도
    - 출석 체크 기능
    
**| 2025.02.17 version 1.2.1 update**