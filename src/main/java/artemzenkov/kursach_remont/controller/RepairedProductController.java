package artemzenkov.kursach_remont.controller;

import artemzenkov.kursach_remont.model.RepairedProductDTO;
import artemzenkov.kursach_remont.service.RepairedProductService;
import artemzenkov.kursach_remont.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/repairedProducts")
public class RepairedProductController {

    private final RepairedProductService repairedProductService;

    public RepairedProductController(final RepairedProductService repairedProductService) {
        this.repairedProductService = repairedProductService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("repairedProducts", repairedProductService.findAll());
        return "repairedProduct/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("repairedProduct") final RepairedProductDTO repairedProductDTO) {
        return "repairedProduct/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("repairedProduct") @Valid final RepairedProductDTO repairedProductDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "repairedProduct/add";
        }
        repairedProductService.create(repairedProductDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("repairedProduct.create.success"));
        return "redirect:/repairedProducts";
    }

    @GetMapping("/edit/{name}")
    public String edit(@PathVariable(name = "name") final Long name, final Model model) {
        model.addAttribute("repairedProduct", repairedProductService.get(name));
        return "repairedProduct/edit";
    }

    @PostMapping("/edit/{name}")
    public String edit(@PathVariable(name = "name") final Long name,
            @ModelAttribute("repairedProduct") @Valid final RepairedProductDTO repairedProductDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "repairedProduct/edit";
        }
        repairedProductService.update(name, repairedProductDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("repairedProduct.update.success"));
        return "redirect:/repairedProducts";
    }

    @PostMapping("/delete/{name}")
    public String delete(@PathVariable(name = "name") final Long name,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = repairedProductService.getReferencedWarning(name);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            repairedProductService.delete(name);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("repairedProduct.delete.success"));
        }
        return "redirect:/repairedProducts";
    }

}
