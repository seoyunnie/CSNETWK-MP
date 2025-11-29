package seoyunnie.pokeprotocol.validator;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class IntegerDocumentFilter extends DocumentFilter {
    private boolean isInt(String str) {
        if (str.isEmpty()) {
            return true;
        }

        try {
            Integer.parseInt(str);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void insertString(FilterBypass filterBypass, int offset, String str, AttributeSet attrSet)
            throws BadLocationException {
        if (isInt(str)) {
            super.insertString(filterBypass, offset, str, attrSet);
        }
    }

    @Override
    public void replace(FilterBypass filterBypass, int offset, int len, String text, AttributeSet attrSet)
            throws BadLocationException {
        if (isInt(text)) {
            super.replace(filterBypass, offset, len, text, attrSet);
        }
    }
}
