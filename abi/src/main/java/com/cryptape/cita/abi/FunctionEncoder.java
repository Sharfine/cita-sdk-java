package com.cryptape.cita.abi;

import java.math.BigInteger;
import java.util.List;

import com.cryptape.cita.abi.datatypes.Function;
import com.cryptape.cita.abi.datatypes.StaticArray;
import com.cryptape.cita.abi.datatypes.Type;
import com.cryptape.cita.abi.datatypes.Uint;
import com.cryptape.cita.crypto.Hash;
import com.cryptape.cita.utils.Numeric;

/**
 * <p>Ethereum Contract Application Binary Interface (ABI) encoding for functions.
 * Further details are available
 * <a href="https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI">here</a>.
 * </p>
 */
public class FunctionEncoder {

    private FunctionEncoder() { }

    public static String encode(Function function) {
        List<Type> parameters = function.getInputParameters();

        String methodSignature = buildMethodSignature(function.getName(), parameters);
        String methodId = buildMethodId(methodSignature);

        StringBuilder result = new StringBuilder();
        result.append(methodId);

        return encodeParameters(parameters, result);
    }

    public static String encodeConstructor(List<Type> parameters) {
        return encodeParameters(parameters, new StringBuilder());
    }

    private static String encodeParameters(List<Type> parameters, StringBuilder result) {
        int dynamicDataOffset = getLength(parameters) * Type.MAX_BYTE_LENGTH;
        StringBuilder dynamicData = new StringBuilder();

        for (Type parameter:parameters) {
            String encodedValue = TypeEncoder.encode(parameter);

            if (TypeEncoder.isDynamic(parameter)) {
                String encodedDataOffset = TypeEncoder.encodeNumeric(
                        new Uint(BigInteger.valueOf(dynamicDataOffset)));
                result.append(encodedDataOffset);
                dynamicData.append(encodedValue);
                dynamicDataOffset += encodedValue.length() >> 1;
            } else {
                result.append(encodedValue);
            }
        }
        result.append(dynamicData);

        return result.toString();
    }

    private static int getLength(List<Type> parameters) {
        int count = 0;
        for (Type type:parameters) {
            if (type instanceof StaticArray) {
                count += ((StaticArray) type).getValue().size();
            } else {
                count++;
            }
        }
        return count;
    }

    static String buildMethodSignature(String methodName, List<Type> parameters) {
        StringBuilder result = new StringBuilder();
        result.append(methodName);
        result.append("(");

        StringBuilder params = new StringBuilder();
        for (int i = 0; i < parameters.size(); i++) {
            params.append(parameters.get(i).getTypeAsString());
            if (i + 1 < parameters.size()) {
                params.append(",");  // no whitespace
            }
        }

        result.append(params.toString());
        result.append(")");
        return result.toString();
    }

    public static String buildMethodId(String methodSignature) {
        byte[] input = methodSignature.getBytes();
        byte[] hash = Hash.sha3(input);
        return Numeric.toHexString(hash).substring(0, 10);
    }
}
