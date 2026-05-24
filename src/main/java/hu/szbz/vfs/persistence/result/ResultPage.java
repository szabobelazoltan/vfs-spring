package hu.szbz.vfs.persistence.result;

import java.util.List;

public record ResultPage<T>(List<T> items, int totalMatches, int pageSize, int pageIndex) {
}
