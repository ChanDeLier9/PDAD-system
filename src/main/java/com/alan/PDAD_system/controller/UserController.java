package com.alan.PDAD_system.controller;

import com.alan.PDAD_system.dto.LoginRequest;
import com.alan.PDAD_system.dto.RegisterRequestDTO;
import com.alan.PDAD_system.dto.Result;
import com.alan.PDAD_system.entity.Doctor;
import com.alan.PDAD_system.entity.Patient;
import com.alan.PDAD_system.entity.User;
import com.alan.PDAD_system.service.DoctorService;
import com.alan.PDAD_system.service.PatientService;
import com.alan.PDAD_system.service.UserService;
import com.alan.PDAD_system.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
    /*@RequestMapping("/user")注解表示该控制器处理所有以
      /user为前缀的URL请求。*/
@RestController
@RequestMapping("/user")  //用户功能的基本路径为：/user
@Validated
public class UserController {
    //实例化UserService接口,以便通过该对象进行相关的操作
        private final UserService userService;
        private final DoctorService doctorService;
        private final PatientService patientService;
        private final BCryptPasswordEncoder passwordEncoder;
        @Autowired
        public UserController(UserService userService, DoctorService doctorService, PatientService patientService, BCryptPasswordEncoder passwordEncoder) {
            this.userService = userService;
            this.doctorService = doctorService;
            this.patientService = patientService;
            this.passwordEncoder = passwordEncoder;
        }
    /*创建register注册函数，该函数有两个传入参数：
     username:用户名 password：用户密码。注：此处函数的参数
     名需要与接口设计中的参数名一致，函数将通过参数名来接收传入
     的key值。比如username必须是这个，不能是userName*/

   /*  @PostMapping("/register")注解表示该方法处理的是POST
     请求，路径为“/user/register”*/

   /* 使用正则表达式验证输入字符串的格式  */
   @PostMapping("/register")
   public Result register(@RequestBody @Valid RegisterRequestDTO registerRequest) {


       // 检查用户ID是否已存在
       User existingUser = userService.findById(registerRequest.getUserId());
       if (existingUser != null) {  // 如果用户ID已存在
           System.out.println("error");
           return Result.error("该账号已经被使用！");
       }

       // 对密码进行加密
       String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());  // 对输入的密码进行加密
       System.out.println("注册时加密后的密码: " + encodedPassword);

       //  创建新用户对象

       User newUser = new User();
       newUser.setUserId(registerRequest.getUserId()); // 使用提供的用户ID作为账号
       newUser.setPassword(encodedPassword);  // 设置加密后的密码
       newUser.setEmail(registerRequest.getEmail());
       newUser.setIdNumber(registerRequest.getIdNumber());
       newUser.setRole(registerRequest.getRole() == null || registerRequest.getRole().isEmpty() ? "doctor" : registerRequest.getRole()); // 默认角色为 doctor
       newUser.setAge(registerRequest.getAge()); // 设置年龄
       newUser.setGender(registerRequest.getGender()); // 设置年龄
       newUser.setStatus("pending");  // 默认设置状态为 'pending'

       userService.registerUser(newUser);

       return Result.success("注册成功！", newUser.getUserId());
   }

        /*login函数的返回值时候Result<String>类型的，即返回的Result对
         * 象中data属性的类型是String。该字符串存放的是JWT令牌。user
         * */
        // 登录接口
        @PostMapping("/login")
        public Result<Map<String, Object>> login(@RequestBody @Valid LoginRequest loginRequest) {


            // 查询用户是否存在
            User loginUser = userService.findById(loginRequest.getDoctorId());
            if (loginUser == null) {
                return Result.error("用户不存在！");
            }

            // 校验密码是否正确
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(loginRequest.getPassword(), loginUser.getPassword())) {
                return Result.error("密码错误！");
            }
// 判断用户角色
            String role = loginUser.getRole();
            if ("doctor".equalsIgnoreCase(role)) {
                Doctor doctor = doctorService.findDoctorById(loginUser.getUserId());
                if (doctor == null) {
                    return Result.error("用户身份错误，未找到对应医生信息！");
                }
            } else if ("patient".equalsIgnoreCase(role)) {
                Patient patient = patientService.findPatientById(loginUser.getUserId());
                if (patient == null) {
                    return Result.error("用户身份错误，未找到对应患者信息！");
                }
            } else {
                return Result.error("用户角色信息异常！");
            }

            // 登录成功，生成 JWT Token（含角色信息）
            String token = generateJwtToken(loginUser);

            // 返回 token、userId和用户角色
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", loginUser.getUserId());
            response.put("role", role);

            return Result.success(response);
        }

        private String generateJwtToken(User user) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getUserId());
            claims.put("role", user.getRole());
            return JwtUtil.genToken(claims, 30); // 生成30分钟有效期的JWT Token
        }


    }



