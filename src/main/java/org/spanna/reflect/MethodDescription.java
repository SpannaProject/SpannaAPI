package org.spanna.reflect;

import org.spanna.util.Util;
import java.util.Objects;

public class MethodDescription {

    public final String name;
    public final String signature;

    public MethodDescription(String name, String signature) {
        this.name = name;
        this.signature = signature;
    }

    public MethodDescription(String name, Object returnType, Object... args) {
        this.name = name;
        this.signature = Util.arrayToSignature(returnType, args);
        System.out.println("[SIG] Signature is " + this.signature);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.signature);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MethodDescription other = (MethodDescription) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.signature, other.signature)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MethodDescription{" + "name=" + name + ", signature=" + signature + '}';
    }

}
