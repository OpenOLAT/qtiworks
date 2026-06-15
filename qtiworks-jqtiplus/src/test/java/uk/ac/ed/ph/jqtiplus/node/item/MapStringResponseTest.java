/* Copyright (c) 2012-2013, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.item;

import static org.junit.Assert.assertEquals;

import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MapStringResponseTest {

    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "MapResponse-Empty.xml", new String[] { "" }, 1.0 },
                { "MapResponse-Empty.xml", new String[] { " " }, 1.0 },
                { "MapResponse-Empty.xml", new String[] { "Z" }, -1.0 },
                { "MapResponse-Empty-Multiple.xml", new String[] { "", "A" }, 2.5 },
                // test especially case sensitivity and trimming
                { "MapResponse-String.xml", new String[] { "Gap" }, 1.0 },
                { "MapResponse-String.xml", new String[] { "Gap " }, 1.0 },
                { "MapResponse-String.xml", new String[] { " Gap" }, 1.0 },
                { "MapResponse-String.xml", new String[] { " gap " }, 1.0 },
                { "MapResponse-String.xml", new String[] { "fill" }, 0.5 },
                { "MapResponse-String.xml", new String[] { " fill " }, 0.5 },
                { "MapResponse-String.xml", new String[] { "Fill" }, -1.0 },
                // Custom OpenOlat feature: ignore spaces
                { "MapResponse-String-ignore-spaces.xml", new String[] { "New York" }, 1.0 },
                { "MapResponse-String-ignore-spaces.xml", new String[] { "NewYork" }, 1.0 },
                { "MapResponse-String-ignore-spaces.xml", new String[] { "new york" }, -1.0 },
                { "MapResponse-String-ignore-spaces.xml", new String[] { "1 2  3\t456\n" }, 1.0 },
                { "MapResponse-String-ignore-spaces.xml", new String[] { "col\nor" }, 0.5 },
                { "MapResponse-String-ignore-spaces.xml", new String[] { "Col\nOr" }, 0.5 },
        });
    }

    private final String fileName;
    private Value response;
    private final double expectedOutcome;

    public MapStringResponseTest(final String fileName, final String[] responses, final double expectedOutcome) {
        this.fileName = fileName;
        this.expectedOutcome = expectedOutcome;

        if (responses.length == 1) {
        	if(responses[0].isEmpty()) {
        		response = NullValue.INSTANCE;
        	} else {
        		response = new StringValue(responses[0]);
        	}
        }
        else {
            final List<SingleValue> values = new ArrayList<>();
            for (final String s : responses) {
            	values.add(new StringValue(s));
            }
            response = MultipleValue.createMultipleValue(values);
        }
    }

    @Test
    public void test() throws Exception {
        final ItemSessionController itemSessionController = UnitTestHelper.loadUnitTestAssessmentItemForControl("item/mapResponse/" + fileName, true);
        final Date timestamp = new Date();
        itemSessionController.initialize(timestamp);
        itemSessionController.performTemplateProcessing(timestamp);
        itemSessionController.enterItem(timestamp);

        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();
        final AssessmentItem item = itemSessionController.getSubjectItem();

        final Identifier responseIdentifier = Identifier.assumedLegal("RESPONSE");
        if (item.getResponseDeclaration(responseIdentifier).getCardinality().isMultiple()) {
        	if(!response.isNull() && response.getCardinality().isSingle()) {
        		response = MultipleValue.createMultipleValue((SingleValue) response);
        	}
        }
        itemSessionState.setResponseValue(responseIdentifier, response);
        itemSessionController.performResponseProcessing(timestamp);

        assertEquals(new FloatValue(expectedOutcome), itemSessionState.getOutcomeValue(Identifier.assumedLegal("OUTCOME")));
    }
}
