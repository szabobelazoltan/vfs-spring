package hu.szbz.vfs.components;

import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectType;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class TreeTraversal {
    private final FileObjectEntityRepository repository;

    public TreeTraversal(FileObjectEntityRepository repository) {
        this.repository = repository;
    }

    public <T> T traverseDown(FileObjectEntity startNode, TreeTraversalProcessor<T> processor) {
        T result = processor.prepareResult();
        Queue<FileObjectEntity> queue = new LinkedList<>();
        queue.offer(startNode);
        while (!queue.isEmpty()) {
            FileObjectEntity currentNode = queue.poll();
            result = processor.processNode(currentNode, result);
            if (currentNode.isDirectory()) {
                repository.findAllByParentAndTypeIn(currentNode, EnumSet.allOf(FileObjectType.class))
                        .forEach(queue::offer);
            }
        }
        return processor.postProcessResult(result);
    }

    public <T> T traverseUp(FileObjectEntity startNode, TreeTraversalProcessor<T> processor) {
        T result = processor.prepareResult();
        FileObjectEntity currentNode = startNode;
        while (currentNode != null) {
            result = processor.processNode(currentNode, result);
            currentNode = currentNode.getParent();
        }
        return processor.postProcessResult(result);
    }

    public interface TreeTraversalProcessor<T> {
        T prepareResult();

        T processNode(FileObjectEntity node, T oldResultValue);

        default T postProcessResult(T result) {
            return result;
        }
    }

    public static class SubTreeCollector implements TreeTraversalProcessor<List<FileObjectEntity>> {
        @Override
        public List<FileObjectEntity> prepareResult() {
            return new LinkedList<>();
        }

        @Override
        public List<FileObjectEntity> processNode(FileObjectEntity node, List<FileObjectEntity> oldResultValue) {
            oldResultValue.add(node);
            return oldResultValue;
        }
    }
}
