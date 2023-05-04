package com.lf.fashion.data.network.api

/**
 * php 백단에서 세션을 만들어서 수명을 조절할 경우 logout 전송이 필요할 수 있어 남겨둠.
 * 현재는 jwt 로만 로그인을 관리중, 로그인 시간 만료시 자동 로그인 구조 (TokenAuthenticator 클래스를 통해 자동 로그인 토큰 연장 방식으로 구현)
 */
interface BaseApi {
}