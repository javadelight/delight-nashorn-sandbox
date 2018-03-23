package delight.nashornsandbox;

import delight.nashornsandbox.internal.RemoveComments;
import delight.nashornsandbox.providers.CommentedProvider;
import delight.nashornsandbox.providers.Pair;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRemoveComments {

    @Test
    @RepeatedTest(CommentedProvider.testCount)
    @ExtendWith(CommentedProvider.class)
    public void test(Pair<String, String> values) {
        assertEquals(values.getFirst(), RemoveComments.perform(values.getSecond()));
    }

}
