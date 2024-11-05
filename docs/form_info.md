# Pull Request, Annotation, Convention 양식
## PR 양식  
__Pull Request는 다음과 같은 구성으로 작성한다.__  
<br>
__제목__    
```
Add: <구현사항 요약> // Add, Update, Bug ect...
```

__내용__  
```
# :: 작업 주제  
**SRPA-??**  : <진행 사항> // 추가, 완료, 버그, 수정 중 ect...   


# :: 구현사항 설명      
<구현사항 설명1> ...  

# :: 보완할점     
<보완사항, 주의사항>      
<추가로 알아야할 사항>    
<br>
```

## KDoc 양식
1. 긴 설명이 필요한 경우
```
/**
* 함수 설명
*
* @param 변수타입 설명
* @return 리턴타입 설명
* @example 함수이름
*/
```
2. 약식으로 코드를 설명하는 경우
```
/**
* 함수 설명 [변수]
*/
```
<br>

## Convention  [![Static Badge](https://img.shields.io/badge/code_convention-kotlin_docs-8A2BE2)](https://kotlinlang.org/docs/coding-conventions.html#source-file-names)

1. 기본은 Camel Case, 상수는 Scream Snake Case, 백킹 프로퍼티는 _이름 / 클래스는 대문자, 패키지는 소문자 시작
2. 파일 이름은 해당 내용을 설명하는 것으로 * Utiil, Data와 같은 단어 지양!
3. 클래스는 명사/명사구, 메서드는 동사/동사구 * 모호한 단어는 지양!
4. 클래스 내용은 속성/초기화 -> Sub Constructor -> Method -> CO 순
5. 긴 Args List / Chained Call / 쿼리의 경우 들여쓰기
6. 한 줄 코드의 경우 중괄호 생략
7. 조건이 3개 이상이면, when / 단순 반복문보다 고차 함수 권장
