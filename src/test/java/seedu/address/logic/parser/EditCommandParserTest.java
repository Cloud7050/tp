package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TestData.EMAIL_DESC_AMY;
import static seedu.address.testutil.TestData.EMAIL_DESC_BOB;
import static seedu.address.testutil.TestData.INDEX_FIRST_CONTACT;
import static seedu.address.testutil.TestData.INDEX_SECOND_CONTACT;
import static seedu.address.testutil.TestData.INDEX_THIRD_CONTACT;
import static seedu.address.testutil.TestData.INVALID_EMAIL_DESC;
import static seedu.address.testutil.TestData.INVALID_NAME_DESC;
import static seedu.address.testutil.TestData.INVALID_PHONE_DESC;
import static seedu.address.testutil.TestData.NAME_DESC_AMY;
import static seedu.address.testutil.TestData.NOTE_DESC_AMY;
import static seedu.address.testutil.TestData.NOTE_DESC_BOB;
import static seedu.address.testutil.TestData.PHONE_DESC_AMY;
import static seedu.address.testutil.TestData.PHONE_DESC_BOB;
import static seedu.address.testutil.TestData.VALID_EMAIL_AMY;
import static seedu.address.testutil.TestData.VALID_NAME_AMY;
import static seedu.address.testutil.TestData.VALID_NOTE_AMY;
import static seedu.address.testutil.TestData.VALID_PHONE_AMY;
import static seedu.address.testutil.TestData.VALID_PHONE_BOB;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditContactDescriptor;
import seedu.address.testutil.EditContactDescriptorBuilder;
import seedu.address.testutil.TestData;



public class EditCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", Messages.MESSAGE_EDIT_COMMAND_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Messages.MESSAGE_NAME_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Messages.MESSAGE_PHONE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Messages.MESSAGE_EMAIL_CONSTRAINTS); // invalid email
        // Invalid tag
        assertParseFailure(
            parser,
            "1" + TestData.Invalid.Tag.FLAG_HASHTAG,
            Messages.tagInvalid(TestData.Invalid.Tag.HASHTAG)
        );

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Messages.MESSAGE_PHONE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Contact} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(
            parser,
            "1"
                    + TestData.Valid.Tag.FLAG
                    + TestData.Valid.Tag.FLAG_ALPHANUMERIC
                    + TestData.Valid.Tag.FLAG_ALPHANUMERIC_SPACES,
            Messages.tagInvalid("")
        );
        assertParseFailure(
            parser,
            "1"
                    + TestData.Valid.Tag.FLAG_ALPHANUMERIC
                    + TestData.Valid.Tag.FLAG
                    + TestData.Valid.Tag.FLAG_ALPHANUMERIC_SPACES,
            Messages.tagInvalid("")
        );
        assertParseFailure(
            parser,
            "1"
                    + TestData.Valid.Tag.FLAG_ALPHANUMERIC
                    + TestData.Valid.Tag.FLAG_ALPHANUMERIC_SPACES
                    + TestData.Valid.Tag.FLAG,
            Messages.tagInvalid("")
        );

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_NOTE_AMY + VALID_PHONE_AMY,
                Messages.MESSAGE_NAME_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_CONTACT;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + TestData.Valid.Tag.FLAG_ALPHANUMERIC
                + EMAIL_DESC_AMY + NOTE_DESC_AMY + NAME_DESC_AMY + TestData.Valid.Tag.FLAG_ALPHANUMERIC_SPACES;

        EditContactDescriptor descriptor = new EditContactDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withNote(VALID_NOTE_AMY)
                .withTags(TestData.Valid.Tag.ALPHANUMERIC, TestData.Valid.Tag.ALPHANUMERIC_SPACES).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_CONTACT;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditContactDescriptor descriptor = new EditContactDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_CONTACT;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditContactDescriptor descriptor = new EditContactDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditContactDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_AMY;
        descriptor = new EditContactDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // note
        userInput = targetIndex.getOneBased() + NOTE_DESC_AMY;
        descriptor = new EditContactDescriptorBuilder().withNote(VALID_NOTE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TestData.Valid.Tag.FLAG_ALPHANUMERIC;
        descriptor = new EditContactDescriptorBuilder().withTags(TestData.Valid.Tag.ALPHANUMERIC).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_CONTACT;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, ArgumentMultimap.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, ArgumentMultimap.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // Duplicated valid tag
        userInput = targetIndex.getOneBased()
                + PHONE_DESC_AMY
                + NOTE_DESC_AMY
                + EMAIL_DESC_AMY
                + TestData.Valid.Tag.FLAG_ALPHANUMERIC
                + PHONE_DESC_AMY
                + NOTE_DESC_AMY
                + EMAIL_DESC_AMY
                + TestData.Valid.Tag.FLAG_ALPHANUMERIC
                + PHONE_DESC_BOB
                + NOTE_DESC_BOB
                + EMAIL_DESC_BOB
                + TestData.Valid.Tag.FLAG_ALPHANUMERIC_SPACES;

        assertParseFailure(parser, userInput,
            ArgumentMultimap.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_NOTE));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + NOTE_DESC_AMY + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + NOTE_DESC_AMY + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
            ArgumentMultimap.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_NOTE));
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_CONTACT;
        String userInput = targetIndex.getOneBased() + TestData.Valid.Tag.FLAG;

        EditContactDescriptor descriptor = new EditContactDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
