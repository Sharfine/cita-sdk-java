package com.citahub.cita.abi.datatypes.generated;

import java.math.BigInteger;
import com.citahub.cita.abi.datatypes.Int;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use com.citahub.cita.codegen.AbiTypesGenerator in the
 */
public class Int72 extends Int {
    public static final Int72 DEFAULT = new Int72(BigInteger.ZERO);

    public Int72(BigInteger value) {
        super(72, value);
    }

    public Int72(long value) {
        this(BigInteger.valueOf(value));
    }
}
