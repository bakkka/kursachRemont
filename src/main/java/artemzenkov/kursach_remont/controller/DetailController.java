package artemzenkov.kursach_remont.controller;

import artemzenkov.kursach_remont.domain.Node;
import artemzenkov.kursach_remont.domain.RepairedProduct;
import artemzenkov.kursach_remont.model.DetailDTO;
import artemzenkov.kursach_remont.repos.NodeRepository;
import artemzenkov.kursach_remont.repos.RepairedProductRepository;
import artemzenkov.kursach_remont.service.DetailService;
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
@RequestMapping("/details")
public class DetailController {

    private final DetailService detailService;
    private final NodeRepository nodeRepository;
    private final RepairedProductRepository repairedProductRepository;

    public DetailController(final DetailService detailService, final NodeRepository nodeRepository,
            final RepairedProductRepository repairedProductRepository) {
        this.detailService = detailService;
        this.nodeRepository = nodeRepository;
        this.repairedProductRepository = repairedProductRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("nodeValues", nodeRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Node::getId, Node::getId)));
        model.addAttribute("repairedProductsValues", repairedProductRepository.findAll(Sort.by("name"))
                .stream()
                .collect(CustomCollectors.toSortedMap(RepairedProduct::getName, RepairedProduct::getName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("details", detailService.findAll());
        return "detail/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("detail") final DetailDTO detailDTO) {
        return "detail/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("detail") @Valid final DetailDTO detailDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "detail/add";
        }
        detailService.create(detailDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("detail.create.success"));
        return "redirect:/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("detail", detailService.get(id));
        return "detail/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("detail") @Valid final DetailDTO detailDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "detail/edit";
        }
        detailService.update(id, detailDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("detail.update.success"));
        return "redirect:/details";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = detailService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            detailService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("detail.delete.success"));
        }
        return "redirect:/details";
    }

}
