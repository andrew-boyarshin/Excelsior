package sh.now.andrew_boyarshin.excelsior.pizza;

import java.util.List;

final class CartesianProduct {
    private final List<List<Direction>> axes;
    private final int[] axesSizeProduct;

    public CartesianProduct(List<List<Direction>> axes) {
        this.axes = axes;

        var axesSizeProduct = new int[axes.size() + 1];
        axesSizeProduct[axes.size()] = 1;
        for (int i = axes.size() - 1; i >= 0; i--) {
            axesSizeProduct[i] = axesSizeProduct[i + 1] * axes.get(i).size();
        }

        this.axesSizeProduct = axesSizeProduct;
    }

    private int getAxisIndexForProductIndex(int index, int axis) {
        return (index / axesSizeProduct[axis + 1]) % axes.get(axis).size();
    }

    public int size() {
        return axesSizeProduct[0];
    }

    public Direction getAt(final int index, int axis) {
        if (axis < 0 || axis >= axes.size()) {
            throw new IndexOutOfBoundsException();
        }

        return axes.get(axis).get(getAxisIndexForProductIndex(index, axis));
    }
}
