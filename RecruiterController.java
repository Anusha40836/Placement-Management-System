package com.placement.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.placement.entities.Jobs;
import com.placement.entities.Recruiter;
import com.placement.entities.Student;
import com.placement.repository.JobRepository;
import com.placement.repository.RecruiterRepository;

@Controller
public class RecruiterController {
	static Path cwd = Path.of("").toAbsolutePath();
	 private static Path UPLOADED_FOLDER = cwd;
	@Autowired
	RecruiterRepository repo;
	@Autowired
	JobRepository jrepo;
	@Autowired
	RecruiterRepository rrepo;
	PasswordEncoder passwordencoder;
	
	
	@RequestMapping("/recruiter/signup")
  	public String signup(Model model)
  	{
		Recruiter r=new Recruiter();
		model.addAttribute("recruiter", r);
  		return "recruiter/rsignup";
  	}
	
	@RequestMapping("/recruiter/save")
	public String save(@ModelAttribute("recruiter") Recruiter r)
	{
		String password=r.getPassword();
		this.passwordencoder= new BCryptPasswordEncoder();
		String hashPassword=passwordencoder.encode(password);
		r.setPassword(hashPassword);
		
		repo.save(r);
		return "recruiter/success";
	}
	
	@RequestMapping("/recruiter/signin")
	public String signin(Model model)
	{
		Recruiter r=new Recruiter();
		model.addAttribute("recruiter", r);
		return "recruiter/rsignin";
	}
	
	
	
	
	@RequestMapping(value="/rvalidation", method=RequestMethod.POST)
	public String rvalidation(@ModelAttribute("recruiter") Recruiter recruiter,HttpSession session,RedirectAttributes ra )
	{
		String email=recruiter.getEmail();
		String password=recruiter.getPassword();
		Recruiter r=repo.findByEmail(email);
		System.out.println(r);
		this.passwordencoder = new BCryptPasswordEncoder();  
		
		boolean isPasswordMatches = passwordencoder.matches(password,r.getPassword());
		System.out.println(isPasswordMatches);
		if(!isPasswordMatches)
		{
			ra.addFlashAttribute("emessage", "Id or password is incorrect");			
			return "redirect:/recruiter/signin";
		}
		else
		{
			session.setAttribute("rname",r.getRname());
			session.setAttribute("rid",r.getRid());
			session.setAttribute("logo",r.getLogo());
			session.setAttribute("status",true);
			return "recruiter/rdashboard";
		}
	}
	

	
	
	
	
	
	@RequestMapping("/rlogout")
	public String logout(RedirectAttributes ra,HttpServletRequest request)
	{    HttpSession session=request.getSession();  
    	 session.invalidate();  
		ra.addFlashAttribute("smessage", "You have been logged out successfully.");
		return "redirect:/recruiter/signin";
	}
	
	
	@RequestMapping("/addnewjob")
	public String addnewjobpageload(Model model,HttpSession session)
	{   
		Jobs j=new Jobs();
		model.addAttribute("job", j);
		return "recruiter/addjob";
	}
	
	@RequestMapping(value="/savejob",method =RequestMethod.POST)
	public String addnewjobpageload(@ModelAttribute("job") Jobs job,RedirectAttributes ra,HttpSession session)
	{  
		long rid=(long)session.getAttribute("rid");
		Recruiter r=rrepo.findById(rid).get();
		job.setRid(rid);
		job.setRlogo(r.getLogo());
		job.setStatus("true");
		jrepo.save(job);
		ra.addFlashAttribute("jsmessage", "New Job Created Successfully..!!!");
		return "redirect:/addnewjob";
		
		
	}
	
	
	@RequestMapping("/getjobs/{rid}")
	public String getJobByRecruiter(@PathVariable long rid,Model model)
	{   
		List<Jobs> jlist=jrepo.getJobsById(rid);
		model.addAttribute("jobs", jlist);
		Recruiter r=repo.findById(rid).get();
		model.addAttribute("recruiter", r);
		return "recruiter/joblist";
	}
	@RequestMapping(value="/ruploadlogo",method = RequestMethod.GET )
	public String loadpicuploadpage()
	{
		
		return "recruiter/ruploadpic";
		
	}
	
	@PostMapping("/rsavepic/{rid}") 
    public String picUpload(@RequestParam("logo") MultipartFile file,
                                   RedirectAttributes redirectAttributes,@PathVariable long rid) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("emessage", "Please select a picture to upload");
            return "redirect:/ruploadlogo";
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER+"/src/main/resources/static/img/" + file.getOriginalFilename());
            Files.write(path, bytes);
            String profile_pic=("../img/" + file.getOriginalFilename());
            redirectAttributes.addFlashAttribute("smessage",
                    "Picture successfully uploaded '" + file.getOriginalFilename() + "'");
            
            Recruiter r=repo.findById(rid).get();
            r.setLogo(profile_pic);
            repo.save(r);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/ruploadlogo";
    }
	
	@RequestMapping(value="/rcpassword",method = RequestMethod.GET )
	public String loadpasswordpage(@ModelAttribute("recruiter") Recruiter recruiter,Model model)
	{
		
		
		model.addAttribute("recruiter", recruiter);
		return "recruiter/rchangepassword";
		
	}
	 
	
	/*
	 * @RequestMapping(value="/rsavepassword/{sid}",method = RequestMethod.POST )
	 * public String cpassword(@ModelAttribute("student") Student
	 * student,@PathVariable (value="sid") long sid,RedirectAttributes ra) { String
	 * password=student.getPassword(); Student s=repo.findById(sid).get();
	 * s.setPassword(password); repo.save(s);
	 * ra.addFlashAttribute("changepass","Password changed successfully"); return
	 * "redirect:/cpassword";
	 * 
	 * }
	 */
	
	
}
