"use client";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { cartApi } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import Link from "next/link";

export default function CartPage() {
  const router = useRouter();
  const { user } = useAuth();
  const [cart, setCart] = useState<any>(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) {
      router.push("/login");
      return;
    }
    loadCart();
  }, [user]);

  const loadCart = async () => {
    try {
      const res = await cartApi.get();
      setCart(res.data);
      setError("");
    } catch (err: any) {
      setError(err.message || "Failed to load cart");
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateQuantity = async (itemId: number, quantity: number) => {
    try {
      const res = await cartApi.updateItem(itemId, { quantity });
      setCart(res.data);
      setMessage("Cart updated successfully");
      setError("");
      setTimeout(() => setMessage(""), 3000);
    } catch (err: any) {
      setError(err.message || "Failed to update item");
      setMessage("");
    }
  };

  const handleRemoveItem = async (itemId: number) => {
    try {
      const res = await cartApi.removeItem(itemId);
      setCart(res.data);
      setMessage("Item removed from cart");
      setError("");
      setTimeout(() => setMessage(""), 3000);
    } catch (err: any) {
      setError(err.message || "Failed to remove item");
      setMessage("");
    }
  };

  const handleClearCart = async () => {
    if (!confirm("Are you sure you want to clear your cart?")) return;
    try {
      await cartApi.clear();
      await loadCart();
      setMessage("Cart cleared successfully");
      setError("");
    } catch (err: any) {
      setError(err.message || "Failed to clear cart");
      setMessage("");
    }
  };

  const handleCheckout = async () => {
    try {
      await cartApi.checkout();
      setMessage("Order placed successfully!");
      setError("");
      setTimeout(() => router.push("/orders"), 2000);
    } catch (err: any) {
      setError(err.message || "Failed to checkout");
      setMessage("");
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        Loading...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <Link href="/" className="text-2xl font-bold">
            Smart Shop
          </Link>
          <div className="flex gap-4">
            <Link href="/" className="text-blue-600 hover:underline">
              Products
            </Link>
            <Link href="/orders" className="text-blue-600 hover:underline">
              Orders
            </Link>
            <Link href="/profile" className="text-blue-600 hover:underline">
              Profile
            </Link>
          </div>
        </div>
      </nav>

      <div className="max-w-6xl mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-6">Shopping Cart</h1>

        {message && (
          <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
            {message}
          </div>
        )}
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {!cart || cart.items.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-8 text-center">
            <p className="text-xl text-gray-600 mb-4">Your cart is empty</p>
            <Link
              href="/"
              className="inline-block bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
            >
              Continue Shopping
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2">
              <div className="bg-white rounded-lg shadow">
                {cart.items.map((item: any) => (
                  <div
                    key={item.id}
                    className="flex items-center gap-4 p-4 border-b"
                  >
                    <div className="flex-1">
                      <Link
                        href={`/products/${item.productId}`}
                        className="text-lg font-semibold hover:text-blue-600"
                      >
                        {item.productName}
                      </Link>
                      <p className="text-gray-600">${item.productPrice} each</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() =>
                          handleUpdateQuantity(
                            item.id,
                            Math.max(1, item.quantity - 1),
                          )
                        }
                        className="bg-gray-200 px-3 py-1 rounded hover:bg-gray-300"
                      >
                        -
                      </button>
                      <span className="px-4">{item.quantity}</span>
                      <button
                        onClick={() =>
                          handleUpdateQuantity(item.id, item.quantity + 1)
                        }
                        className="bg-gray-200 px-3 py-1 rounded hover:bg-gray-300"
                      >
                        +
                      </button>
                    </div>
                    <div className="text-right">
                      <p className="text-lg font-bold">
                        ${item.totalPrice.toFixed(2)}
                      </p>
                      <button
                        onClick={() => handleRemoveItem(item.id)}
                        className="text-red-600 hover:underline text-sm"
                      >
                        Remove
                      </button>
                    </div>
                  </div>
                ))}
              </div>
              <button
                onClick={handleClearCart}
                className="mt-4 text-red-600 hover:underline"
              >
                Clear Cart
              </button>
            </div>

            <div className="lg:col-span-1">
              <div className="bg-white rounded-lg shadow p-6 sticky top-4">
                <h2 className="text-xl font-bold mb-4">Order Summary</h2>
                <div className="space-y-2 mb-4">
                  <div className="flex justify-between">
                    <span>Items ({cart.totalItems}):</span>
                    <span>${cart.totalAmount.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between font-bold text-lg border-t pt-2">
                    <span>Total:</span>
                    <span>${cart.totalAmount.toFixed(2)}</span>
                  </div>
                </div>
                <button
                  onClick={handleCheckout}
                  className="w-full bg-blue-600 text-white py-3 rounded hover:bg-blue-700 font-semibold"
                >
                  Proceed to Checkout
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
