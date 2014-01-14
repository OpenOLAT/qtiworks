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
package uk.ac.ed.ph.qtiworks.services.candidate;

/**
 * Represents the various privileges a candidate user can have against a particular session.
 *
 * FIXME: Some of these represent "bad request" or "bad state" rather than "not allowed", so should
 * possibly be handled better (e.g. sending a 400 response). In that case, it would make sense to merge
 * {@link CandidateSessionTerminatedException} in here, as that's effectively the same thing.
 *
 * @see CandidateForbiddenException
 *
 * @author David McKain
 */
public enum CandidatePrivilege {

    /* General */
    ACCESS_CANDIDATE_SESSION,
    ACCESS_CANDIDATE_SESSION_AS_ITEM,
    ACCESS_CANDIDATE_SESSION_AS_TEST,
    ACCESS_ENTERED_SESSION,
    ACCESS_AUTHOR_INFO,

    /* Items */
    MAKE_RESPONSES,
    SUBMIT_COMMENT,
    ACCESS_BLACKLISTED_ASSESSMENT_FILE,
    ACCESS_ASSESSMENT_FILE,
    END_SESSION_WHEN_INTERACTING,
    END_SESSION_WHEN_ENDED,
    SOFT_RESET_SESSION_WHEN_INTERACTING,
    SOFT_RESET_SESSION_WHEN_ENDED,
    HARD_RESET_SESSION_WHEN_INTERACTING,
    HARD_RESET_SESSION_WHEN_ENDED,
    SOLUTION_WHEN_INTERACTING,
    SOLUTION_WHEN_ENDED,

    /* Tests */
    SELECT_NONLINEAR_MENU,
    SELECT_NONLINEAR_TEST_ITEM,
    FINISH_LINEAR_TEST_ITEM,
    END_TEST_PART,
    REVIEW_TEST_PART,
    REVIEW_TEST_ITEM,
    SOLUTION_TEST_ITEM,
    ADVANCE_TEST_PART,
    EXIT_TEST,

    ;

}
