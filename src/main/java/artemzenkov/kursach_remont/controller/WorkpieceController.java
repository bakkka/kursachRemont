package artemzenkov.kursach_remont.controller;

import artemzenkov.kursach_remont.domain.Detail;
import artemzenkov.kursach_remont.model.WorkpieceDTO;
import artemzenkov.kursach_remont.repos.DetailRepository;
import artemzenkov.kursach_remont.service.WorkpieceService;
import artemzenkov.kursach_remont.util.CustomCollectors;
import artemzenkov.kursach_remont.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/workpieces")
public class WorkpieceController {

    private final WorkpieceService workpieceService;
    private final DetailRepository detailRepository;

    public WorkpieceController(final WorkpieceService workpieceService,
            final DetailRepository detailRepository) {
        this.workpieceService = workpieceService;
        this.detailRepository = detailRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("detailValues", detailRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Detail::getId, Detail::getName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("workpieces", workpieceService.findAll());
        return "workpiece/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("workpiece") final WorkpieceDTO workpieceDTO) {
        return "workpiece/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("workpiece") @Valid final WorkpieceDTO workpieceDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "workpiece/add";
        }
        workpieceService.create(workpieceDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("workpiece.create.success"));
        return "redirect:/workpieces";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("workpiece", workpieceService.get(id));
        return "workpiece/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("workpiece") @Valid final WorkpieceDTO workpieceDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "workpiece/edit";
        }
        workpieceService.update(id, workpieceDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("workpiece.update.success"));
        return "redirect:/workpieces";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        workpieceService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("workpiece.delete.success"));
        return "redirect:/workpieces";
    }

}
