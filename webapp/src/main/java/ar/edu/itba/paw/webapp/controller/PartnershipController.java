package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.PartnershipRequestService;
import ar.edu.itba.paw.webapp.form.PartnershipRequestForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class PartnershipController {
    private final PartnershipRequestService partnershipRequestService;

    @GetMapping("/partnership")
    @PreAuthorize("permitAll()")
    public ModelAndView partnershipsGET() {
        final ModelAndView mav = new ModelAndView("partnership/partnership");
        mav.addObject("partnershipRequestForm", new PartnershipRequestForm());
        return mav;
    }

    @PostMapping("/partnership")
    @PreAuthorize("permitAll()")
    public ModelAndView partnershipPOST(
            @Valid @ModelAttribute final PartnershipRequestForm form,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            ModelAndView mav = new ModelAndView("partnership/partnership");
            mav.addObject("partnershipRequestForm", form);
            return mav;
        }

        partnershipRequestService.createRequest(
            form.getRequestedName(),
            form.getRequestedDomainName(),
            form.getRequestedAdminEmail()
        );

        return new ModelAndView("partnership/partnership_dialog_close");
    }
}
