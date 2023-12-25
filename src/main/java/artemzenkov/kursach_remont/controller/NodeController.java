package artemzenkov.kursach_remont.controller;

import artemzenkov.kursach_remont.model.NodeDTO;
import artemzenkov.kursach_remont.service.NodeService;
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
@RequestMapping("/nodes")
public class NodeController {

    private final NodeService nodeService;

    public NodeController(final NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("nodes", nodeService.findAll());
        return "node/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("node") final NodeDTO nodeDTO) {
        return "node/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("node") @Valid final NodeDTO nodeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "node/add";
        }
        nodeService.create(nodeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("node.create.success"));
        return "redirect:/nodes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("node", nodeService.get(id));
        return "node/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("node") @Valid final NodeDTO nodeDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "node/edit";
        }
        nodeService.update(id, nodeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("node.update.success"));
        return "redirect:/nodes";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = nodeService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            nodeService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("node.delete.success"));
        }
        return "redirect:/nodes";
    }

}
