package hu.szbz.vfs.components;

import hu.szbz.vfs.persistence.model.FileObjectType;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import hu.szbz.vfs.testing.FileObjectEntityBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TreeTraversalTest {
    @Mock
    private FileObjectEntityRepository repository;

    @Spy
    private TreeTraversal.TreeTraversalProcessor<Integer> processor;

    private TreeTraversal testSubject;

    @BeforeEach
    void setUp() {
        this.testSubject = new TreeTraversal(repository);
    }

    @Test
    void traverseDown() {
        var root = new FileObjectEntityBuilder(FileObjectType.DIRECTORY)
                .build();
        var sub = new FileObjectEntityBuilder(FileObjectType.DIRECTORY)
                .build();
        when(repository.findAllByParentAndTypeIn(eq(root), anySet())).thenReturn(List.of(sub));
        var leaf = new FileObjectEntityBuilder(FileObjectType.FILE)
                .build();
        when(repository.findAllByParentAndTypeIn(eq(sub), anySet())).thenReturn(List.of(leaf));

        int expected = 100;
        when(processor.prepareResult()).thenReturn(expected);
        when(processor.processNode(any(), eq(expected))).thenReturn(expected);

        int result = testSubject.traverseDown(root, processor);

        assertEquals(expected, result);
        verify(processor).processNode(root, expected);
        verify(processor).processNode(sub, expected);
        verify(processor).processNode(leaf, expected);
    }

    @Test
    void traverseUp() {
        var root = new FileObjectEntityBuilder(FileObjectType.DIRECTORY)
                .build();
        var sub = new FileObjectEntityBuilder(FileObjectType.DIRECTORY)
                .withParent(root)
                .build();
        var leaf = new FileObjectEntityBuilder(FileObjectType.FILE)
                .withParent(sub)
                .build();

        int expected = 100;
        when(processor.prepareResult()).thenReturn(expected);
        when(processor.processNode(any(), eq(expected))).thenReturn(expected);

        int result = testSubject.traverseUp(leaf, processor);

        assertEquals(expected, result);
        verify(processor).processNode(root, expected);
        verify(processor).processNode(sub, expected);
        verify(processor).processNode(leaf, expected);
    }
}
