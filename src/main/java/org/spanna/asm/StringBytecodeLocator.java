package org.spanna.asm;

import org.objectweb.asm.MethodVisitor;

/**
 * Searches for a string anywhere in a class's methods.
 */
public class StringBytecodeLocator extends BytecodeLocator {

    public static enum SearchMode {

        STARTS_WITH, ENDS_WITH, CONTAINS, EQUALS, EQUALS_IGNORE_CASE;
    }

    private final String search;
    private final SearchMode searchMode;

    public StringBytecodeLocator(String search) {
        super();
        this.search = search;
        this.searchMode = SearchMode.EQUALS;
    }

    public StringBytecodeLocator(String search, SearchMode mode) {
        super();
        this.search = search;
        this.searchMode = mode;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        super.visitMethod(access, name, desc, signature, exceptions);
        return new MethodVisitor(api) {
            @Override
            public void visitLdcInsn(Object cst) {
                if (!(cst instanceof String)) {
                    return;
                }

                String s = (String) cst;

                switch (searchMode) {
                    case STARTS_WITH:
                        if (s.startsWith(search)) {
                            setFound();
                        }
                        break;
                    case ENDS_WITH:
                        if (s.endsWith(search)) {
                            setFound();
                        }
                        break;
                    case CONTAINS:
                        if (s.contains(search)) {
                            setFound();
                        }
                        break;
                    case EQUALS:
                        if (s.equals(search)) {
                            setFound();
                        }
                        break;
                    case EQUALS_IGNORE_CASE:
                        if (s.equalsIgnoreCase(search)) {
                            setFound();
                        }
                        break;
                }
                
                super.visitLdcInsn(cst);
            }
        };
    }
}
