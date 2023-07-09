package com.project.userService.controllers;

//@RestController
//@RequestMapping("/login")
public class Auth {

//    @Value("${telegram.bot.token}")
//    private String botToken;

//    @GetMapping
//    public void test(@RequestParam("id") int id,
//                     @RequestParam("first_name") String firstName,
//                     @RequestParam("auth_date") int authDate,
//                     @RequestParam("hash") String hash,
//                     @RequestParam(value = "username", required = false) String username,
//                     @RequestParam(value = "photo_url", required = false) String photoUrl,
//                     @RequestParam(value = "last_name", required = false) String lastName) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
//        Map<String, String> map = new HashMap<>();
//        map.put("auth_date", String.valueOf(authDate));
//        map.put("first_name", firstName);
//        map.put("id", String.valueOf(id));
//        if (lastName != null) {
//            map.put("last_name", lastName);
//        }
//        if (photoUrl != null) {
//            map.put("photo_url", photoUrl);
//        }
//        if (username != null) {
//            map.put("username", username);
//        }
//        String[] strings = new String[map.size()];
//        int iter = 0;
//        for (String key : map.keySet().stream().sorted().toList()) {
//            var value = map.get(key);
//            strings[iter++] = key + "=" + value;
//        }
//        String str = String.join("\n", strings);
//
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        byte[] keymd = digest.digest(botToken.getBytes(StandardCharsets.UTF_8));
//        var key = Hashing.sha256()
//                .hashString(botToken, StandardCharsets.UTF_8)
//                .asBytes();
//
//
//        Mac mac = Mac.getInstance("HmacSHA256");
//        SecretKeySpec spec = new SecretKeySpec(key, "HmacSHA256");
//        mac.init(spec);
//        byte[] byteHMAC = mac.doFinal(str.getBytes(StandardCharsets.UTF_8));
//        String decode = new String(byteHMAC);
//
////        SecretKeySpec secretKeySpec = new SecretKeySpec(key,HMAC_SHA256);
////        Mac mac = Mac.getInstance(HMAC_SHA256);
////        mac.init(secretKeySpec);
////        var hex =toHexString(mac.doFinal(str.getBytes()));
//
//        System.out.println(Arrays.equals(byteHMAC, hash.getBytes()));
//    }
}
