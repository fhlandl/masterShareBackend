package toy.masterShareBackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@NoArgsConstructor
public class PageResponseDto<E> {

    static int PAGE_LEN = 1;

    private List<E> dataList;

//    private List<Integer> pageNumList;

    private PageRequestDto pageRequest;

    private boolean hasPrev, hasNext;

    private int totalDataCount, currentPage;

    private Integer prevPage, nextPage;

    public PageResponseDto(List<E> dataList, PageRequestDto pageRequest, long totalDataCount) {

        this.dataList = dataList;
        this.pageRequest = pageRequest;
        this.totalDataCount = (int) totalDataCount;

        int end = (int) (Math.ceil(pageRequest.getPage() / (double) PAGE_LEN)) * PAGE_LEN;
        int start = end - PAGE_LEN + 1;
        int last = (int) (Math.ceil((totalDataCount / (double) pageRequest.getSize())));
        if (end > last) {
            end = last;
        }

        this.hasPrev = start > 1;
        this.hasNext = totalDataCount > end * pageRequest.getSize();
//        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

        if (hasPrev) {
            this.prevPage = start - 1;
        }

        if (hasNext) {
            this.nextPage = end + 1;
        }

        this.currentPage = pageRequest.getPage();
    }
}
