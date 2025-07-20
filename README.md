# JWT 관련 의존성

dependencies{
//  핵심 API (JWT 생성, 파싱 등)
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'

//  내부 구현체 (암호화/서명 등 실제 기능)
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'

//  JSON 파싱을 위한 Jackson 지원
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
}

# 원래는 SecurityConfig에서 다 끝내는거가능

# Security 패키지 내부 설명

cf)   @Configuration // 해당 클래스는 설정 클래스이다 

1. WebConfig   -> CORS 설정 위해서 만들었다고 생각하면됨 , http://localhost:5173 이주소는 react local주소이니까 배포시에는 배포용 주소 적용하면됨
                  implements WebMvcConfigurer 하고 Override addCorsMappings 이걸로 cors설정해서 프론트랑 백이 연결되었다고 생각하면 편함
                  

2. SecurityConfig  -> 진짜 그냥 권한 필요한지 아닌지 분류하는 느낌으로 이해하면 좋음
                       permitAll: 걍 다됨 / authenticated : 인증 필요 = 토큰 필요하다   ex)  회원가입은 permitAll   .requestMatchers("POST", "/api/user/register").permitAll() => 회원등록에 토큰이 필요하면 가입을 못하니까
                       토큰은 로그인시 생긴다고 생각하면 편함

 

3. JwtAuthFilter  -> 이게 인증 필터인데 원래는 SecurityConfig만으로 충분한데 , 뭔가 강사님이 개선중이래요 .
                     역할 : Spring Security에서 HTTP 요청을 가로채서 JWT 토큰을 검증하고 인증 처리를 직접 (OncePerRequestFilter를 클래스 상속받아서 그렇다함 cf)요청마다 한 번만 실행되는 필터)
  shouldNotFilter : 인증 
  doFilterInternal :
   // 위에 두개 요약 : 인증된 토큰 맞음? (첫번째 : 있는지 검사 , 두번째: 유효한 토큰인지 검사 cf)application.properties 보면 expiration이거 지나면 만료된 토큰 )
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"인증 토큰이 필요합니다.\"}");
            return;
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"유효하지 않은 토큰입니다.\"}");
            return;
        }

  // 아래는 그냥 이거 안하면  인증됬느데 Spring Security가 모른다함 , @AuthenticationPrincipal, hasRole, .authenticated() 등 전부 인증 안 된 걸로 처리됨 
  >> 이렇다함 나도 잘 모름 그냥 보안상 해야한다고 생각하기

  // 토큰에서 userId 가지고 오는거 세션처럼 생각하면 편함
  // SecurityContextHolder	인증 정보 저장 (세션처럼 작동)  , "이 요청은 누가 한 건지"를 시스템에 알려줘야함
        Integer userId = jwtUtil.getUserId(token); // 누가 
        Authentication auth = new UsernamePasswordAuthenticationToken(
            userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth); // 했는지 등록
        filterChain.doFilter(request, response); // 필터가 끝났으니 다음 필터나 컨트롤러로 넘김


4. JwtUtil
   - generateToken : 토큰 생성 ** userId 기반 
   - getUserId : 토큰에서 정보 가져오기 
   - validateToken : 토큰 유효성 검사

  현재 토큰에는 userId 만 있기 때문에 편하게 객체로 user의 정보를 보낸다고 생각하면

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByAccountName(loginRequest.getAccountName());
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
        String token = jwtUtil.generateToken(user.getId());   
        return ResponseEntity.ok(new LoginResponse(true, token, user.getNickname()));
    }

    String token = jwtUtil.generateToken(user.getId());    >> 이부분을 user.getId -> Integer Id로 아래와 같이 받고있으므로 


        public String generateToken(Integer userId) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

 String token = jwtUtil.generateToken(user의 정보Dto);   >> 이런식으로 정보 막 넣은 Dto를 만들거나 Map을 만들어서 필요한 정보 넣고
 토큰 만들면됨 , 바뀌는 예시

# JwtUtil 부분 ( 수정 예시 )

 public String generateToken(User user) {
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
    return Jwts.builder()
            .subject(String.valueOf(user.getId()))  // 기본 subject: ID
            .claim("nickname", user.getNickname())  // 사용자 이름
            .claim("email", user.getEmail())        // 이메일
            .claim("gender", user.getGender())      // 성별 등
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(key)
            .compact();
}

public Map<String, Object> getUserFromToken(String token) {
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("id", Integer.valueOf(claims.getSubject()));
    userInfo.put("nickname", claims.get("nickname"));
    userInfo.put("email", claims.get("email"));
    userInfo.put("gender", claims.get("gender"));
    return userInfo;
}

#  로그인 부분 수정 예시   //  정보를 줄때 Dto를 만들어서 보내주는 방식 : 그냥 유저Info Dto 혹은 비밀번호같은정보만 빼고 UserFullInfoDto 이런 느낌으로 한다고 생각하면됨
public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    User user = userService.findByAccountName(loginRequest.getAccountName());
    if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
        return ResponseEntity.status(401).body(new ApiResponse(false, "아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    String token = jwtUtil.generateToken(user);  // userId → user 객체로 변경
    UserDto userDto = new UserDto(user);

    return ResponseEntity.ok(new LoginResponse(true, token, userDto));
}

    
