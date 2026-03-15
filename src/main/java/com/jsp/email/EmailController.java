package com.jsp.email;

import java.time.Duration;
import java.util.Random;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmailController {

	private final JavaMailSender mailSender;
	private final StringRedisTemplate redisTemplate;

	@GetMapping("/")
	public String load() {
		return "mail.html";
	}

	@GetMapping("/welcome")
	@ResponseBody
	public String loadWelcome() {
		return "Done !!!";
	}

	@GetMapping("/otp")
	public String otp() {
		return "otp.html";
	}

	@GetMapping("/send-otp")
	public String sendOtp(UserDto dto, RedirectAttributes attributes) {
		int otp = generateOtp();
		sendEmail(otp, dto);
		storeInRedis(dto.getEmail(), otp);
		attributes.addFlashAttribute("email", dto.getEmail());
		return "redirect:/otp";
	}

	@PostMapping("/otp")
	public String otp(@RequestParam String email, @RequestParam int otp, RedirectAttributes attributes) {
		int storedOtp = getFromRedis(email);
		if (storedOtp == 0) {
			attributes.addFlashAttribute("message", "Otp Expired Try Again");
			return "redirect:/";
		} else {
			if (storedOtp == otp) {
				attributes.addFlashAttribute("message", "Register Success");
				return "redirect:/welcome";
			} else {
				attributes.addFlashAttribute("message", "OTP Missmatch");
				attributes.addFlashAttribute("email", email);
				return "redirect:/otp";
			}
		}
	}

	private int getFromRedis(String email) {
		String otp = redisTemplate.opsForValue().get(email);
		if (otp != null)
			return Integer.parseInt(otp);
		else
			return 0;
	}

	private void storeInRedis(String email, int otp) {
		redisTemplate.opsForValue().set(email, otp + "", Duration.ofMinutes(1));
	}

	private void sendEmail(int otp, UserDto dto) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom("admin@ecommerce.com", "Ecommerce APP");
			helper.setTo(dto.getEmail());
			helper.setSubject("Verification of Email thru OTP");
			String prefix = dto.getGender().equals("MALE") ? "Mr." : "Ms.";
			String content = "<html>" + "<head><style>" + "body{background:#f4f6f8;font-family:Arial;}"
					+ ".container{max-width:520px;margin:30px auto;background:#fff;border-radius:8px;}"
					+ ".header{background:#4f46e5;color:#fff;padding:20px;text-align:center;}"
					+ ".content{padding:25px;color:#333;font-size:15px;}"
					+ ".otp{margin:20px 0;text-align:center;font-size:28px;font-weight:bold;letter-spacing:6px;color:#4f46e5;}"
					+ "</style></head>" + "<body>" + "<div class='container'>"
					+ "<div class='header'><h1>Account Verification</h1></div>" + "<div class='content'>"
					+ "<p>Hello <strong>" + prefix + " " + dto.getName() + "</strong>,</p>" + "<p>Your OTP is:</p>"
					+ "<div class='otp'>" + otp + "</div><span>It is Valid Only for 1 Mins</span>" + "</div>" + "</div>"
					+ "</body></html>";

			helper.setText(content, true);

			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int generateOtp() {
		return new Random().nextInt(100000, 1000000);
	}
}
