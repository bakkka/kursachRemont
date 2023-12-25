package artemzenkov.kursach_remont.service;

import artemzenkov.kursach_remont.domain.Detail;
import artemzenkov.kursach_remont.domain.Node;
import artemzenkov.kursach_remont.model.NodeDTO;
import artemzenkov.kursach_remont.repos.DetailRepository;
import artemzenkov.kursach_remont.repos.NodeRepository;
import artemzenkov.kursach_remont.util.NotFoundException;
import artemzenkov.kursach_remont.util.WebUtils;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class NodeService {

    private final NodeRepository nodeRepository;
    private final DetailRepository detailRepository;

    public NodeService(final NodeRepository nodeRepository,
            final DetailRepository detailRepository) {
        this.nodeRepository = nodeRepository;
        this.detailRepository = detailRepository;
    }

    public List<NodeDTO> findAll() {
        final List<Node> nodes = nodeRepository.findAll(Sort.by("id"));
        return nodes.stream()
                .map(node -> mapToDTO(node, new NodeDTO()))
                .toList();
    }

    public NodeDTO get(final Long id) {
        return nodeRepository.findById(id)
                .map(node -> mapToDTO(node, new NodeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final NodeDTO nodeDTO) {
        final Node node = new Node();
        mapToEntity(nodeDTO, node);
        return nodeRepository.save(node).getId();
    }

    public void update(final Long id, final NodeDTO nodeDTO) {
        final Node node = nodeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(nodeDTO, node);
        nodeRepository.save(node);
    }

    public void delete(final Long id) {
        nodeRepository.deleteById(id);
    }

    private NodeDTO mapToDTO(final Node node, final NodeDTO nodeDTO) {
        nodeDTO.setId(node.getId());
        nodeDTO.setWearPercentage(node.getWearPercentage());
        nodeDTO.setReplacementTime(node.getReplacementTime());
        return nodeDTO;
    }

    private Node mapToEntity(final NodeDTO nodeDTO, final Node node) {
        node.setWearPercentage(nodeDTO.getWearPercentage());
        node.setReplacementTime(nodeDTO.getReplacementTime());
        return node;
    }

    public String getReferencedWarning(final Long id) {
        final Node node = nodeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Detail nodeDetail = detailRepository.findFirstByNode(node);
        if (nodeDetail != null) {
            return WebUtils.getMessage("node.detail.node.referenced", nodeDetail.getId());
        }
        return null;
    }

}
