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
package uk.ac.ed.ph.qtiworks.test.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.reading.AssessmentObjectXmlLoader;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment.Feature;
import uk.ac.ed.ph.qtiworks.test.utils.TestUtils;

/**
 * Base class for integration tests that run on a {@link QtiSampleAssessment}
 *
 * @author David McKain
 */
@RunWith(Parameterized.class)
public abstract class AbstractIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);
   
    public static int MAX_TEXT_STREAM_SIZE = 1024 * 1024;
    
    protected final QtiSampleAssessment qtiSampleAssessment;
    protected final URI sampleResourceUri;
    protected final ResourceLocator sampleResourceLocator;
    protected final QtiXmlReader sampleXmlReader;
    protected final JqtiExtensionManager jqtiExtensionManager;

    protected AbstractIntegrationTest(final QtiSampleAssessment qtiSampleAssessment) {
        this.jqtiExtensionManager = TestUtils.buildJqtiExtensionManager(qtiSampleAssessment);
        this.qtiSampleAssessment = qtiSampleAssessment;
        this.sampleResourceUri = qtiSampleAssessment.assessmentClassPathUri();
        this.sampleResourceLocator = new ClassPathResourceLocator();
        this.sampleXmlReader = new QtiXmlReader(jqtiExtensionManager);
    }

    @Before
    public void before() {
        /* If we required MathAssess extensions, check whether Maxima is available and
         * skip the test if not. (This allows people to build the project successfully
         * if they don't have Maxima or don't want the Maxima extensions.)
         */
        if (qtiSampleAssessment.hasFeature(Feature.REQUIRES_MATHASSES)) {

            /* Configuration failed, so use JUnit's Assume class to skip */
            logger.warn("Maxima is not supported, so skipping this test as it requires the MathAssess extensions");
        }
        jqtiExtensionManager.init();
    }

    @After
    public void after() {
        jqtiExtensionManager.destroy();
    }

    /* NB: This assumes all of our samples are UTF-8! */
    protected String readSampleXmlSource() throws IOException {
        return readUnicodeStream(sampleResourceLocator.findResource(sampleResourceUri));
    }

    protected XmlReadResult readSampleXml() throws Exception {
        return sampleXmlReader.read(sampleResourceLocator, sampleResourceUri, true, true);
    }

    protected QtiObjectReader createSampleQtiObjectReader(final boolean schemaValidating) {
        return sampleXmlReader.createQtiObjectReader(sampleResourceLocator, schemaValidating, true);
    }

    protected AssessmentObjectXmlLoader createSampleAssessmentXmlLoader() {
        return new AssessmentObjectXmlLoader(sampleXmlReader, sampleResourceLocator);
    }

    protected AssessmentObjectValidationResult<?> validateSampleObject() {
        AssessmentObjectValidationResult<?> result = null;
        switch (qtiSampleAssessment.getType()) {
            case ASSESSMENT_ITEM:
                result = createSampleAssessmentXmlLoader().loadResolveAndValidateItem(sampleResourceUri);
                break;

            case ASSESSMENT_TEST:
                result = createSampleAssessmentXmlLoader().loadResolveAndValidateTest(sampleResourceUri);
                break;

            default:
                throw new QtiLogicException("Unexpected switch case " + qtiSampleAssessment.getType());
        }
        return result;
    }

    protected ItemSessionController createItemSessionController(final boolean isValid) {
        final ResolvedAssessmentItem resolvedAssessmentItem = createSampleAssessmentXmlLoader().loadAndResolveAssessmentItem(sampleResourceUri);
        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(resolvedAssessmentItem, isValid).initialize();
        final ItemSessionState itemSessionState = new ItemSessionState();
        return new ItemSessionController(jqtiExtensionManager, itemSessionControllerSettings, itemProcessingMap, itemSessionState);
    }
    
    //----------------------------------------------------------------------------
    // Reading methods
    
    /**
     * Reads all character data from the given Reader, returning a String
     * containing all of the data. The Reader will be buffered for efficiency and
     * will be closed once finished with.
     * Be careful reading in very large files - we will barf if MAX_FILE_SIZE is
     * passed as a safety precaution.
     *
     * @param reader source of string data
     * @return String representing the data read
     * @throws IOException
     */
    public static String readCharacterStream(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        int size = 0;
        StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            size += line.length() + 1;
            if (size > MAX_TEXT_STREAM_SIZE) {
                throw new IOException("String data exceeds current maximum safe size ("
                        + MAX_TEXT_STREAM_SIZE + ")");
            }
            result.append(line).append("\n");
        }
        bufferedReader.close();
        return result.toString();
    }

    /**
     * Same as {@link #readCharacterStream(Reader)} but assumes the
     * stream is encoded as UTF-8.
     *
     * @param in InputStream supplying character data
     * @return String representing the data read in
     * @throws IOException
     */
    public static String readUnicodeStream(InputStream in) throws IOException {
        return readCharacterStream(new InputStreamReader(in, "UTF-8"));
    }

    /**
     * Same as {@link #readUnicodeStream(InputStream)} but accepts a plain
     * File object for convenience
     *
     * @param file File to read from
     * @return String representing the data we read in
     * @throws IOException
     */
    public static String readUnicodeFile(File file) throws IOException {
        InputStream inStream = new FileInputStream(file);
        try {
            return readUnicodeStream(inStream);   
        }
        finally {
            inStream.close();
        }
    }
}
