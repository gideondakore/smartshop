"use client";
import { useState, useEffect } from "react";
import { useAuth } from "@/lib/auth-context";
import { orderApi, reviewApi } from "@/lib/api";
import { useRouter } from "next/navigation";
import Link from "next/link";

export default function CustomerDashboard() {
  const { user, logout } = useAuth();
  const router = useRouter();
  const [orders, setOrders] = useState<any[]>([]);
  const [reviews, setReviews] = useState<any[]>([]);
  const [tab, setTab] = useState("orders");

  useEffect(() => {
    if (!user || user.role !== "CUSTOMER") {
      router.push("/");
      return;
    }
    loadData();
  }, [user, router, tab]);

  const loadData = async () => {
    try {
      if (tab === "orders") {
        const res = await orderApi.getUserOrders();
        setOrders(Array.isArray(res.data) ? res.data : res.data.content || []);
      } else if (tab === "reviews") {
        const res = await reviewApi.getUserReviews();
        setReviews(Array.isArray(res.data) ? res.data : res.data.content || []);
      }
    } catch (err) {
      console.error("Failed to load data", err);
    }
  };

  const deleteReview = async (id: number) => {
    if (confirm("Delete this review?")) {
      try {
        await reviewApi.delete(id);
        setReviews(reviews.filter((r) => r.id !== id));
      } catch (err) {
        alert("Failed to delete review");
      }
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-bold">My Dashboard</h1>
          <div className="flex gap-4">
            <Link href="/" className="text-blue-600 hover:underline">
              Home
            </Link>
            <Link href="/cart" className="text-blue-600 hover:underline">
              🛒 Cart
            </Link>
            <Link href="/profile" className="text-blue-600 hover:underline">
              Profile
            </Link>
            <button onClick={logout} className="text-red-600 hover:underline">
              Logout
            </button>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 py-8">
        <h2 className="text-2xl font-bold mb-6">Welcome, {user?.firstName}!</h2>

        {/* Tab Navigation */}
        <div className="flex gap-2 mb-6">
          {["orders", "reviews"].map((t) => (
            <button
              key={t}
              onClick={() => setTab(t)}
              className={`px-4 py-2 rounded ${tab === t ? "bg-blue-600 text-white" : "bg-white"}`}
            >
              {t.charAt(0).toUpperCase() + t.slice(1)}
            </button>
          ))}
        </div>

        {/* Orders Tab */}
        {tab === "orders" && (
          <div className="bg-white rounded-lg shadow overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-100">
                <tr>
                  <th className="px-4 py-2 text-left">Order ID</th>
                  <th className="px-4 py-2 text-left">Total Amount</th>
                  <th className="px-4 py-2 text-left">Status</th>
                  <th className="px-4 py-2 text-left">Date</th>
                  <th className="px-4 py-2 text-left">Actions</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <tr key={order.id} className="border-t">
                    <td className="px-4 py-2">{order.id}</td>
                    <td className="px-4 py-2">${order.totalAmount}</td>
                    <td className="px-4 py-2">
                      <span
                        className={`px-2 py-1 rounded text-sm ${
                          order.status === "DELIVERED"
                            ? "bg-green-100 text-green-800"
                            : order.status === "PENDING"
                              ? "bg-yellow-100 text-yellow-800"
                              : "bg-blue-100 text-blue-800"
                        }`}
                      >
                        {order.status}
                      </span>
                    </td>
                    <td className="px-4 py-2">
                      {new Date(order.createdAt).toLocaleDateString()}
                    </td>
                    <td className="px-4 py-2">
                      <Link
                        href={`/admin/orders/${order.id}`}
                        className="text-blue-600 hover:underline"
                      >
                        View Details
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {orders.length === 0 && (
              <p className="text-center py-8 text-gray-500">
                No orders yet. Start shopping!
              </p>
            )}
          </div>
        )}

        {/* Reviews Tab */}
        {tab === "reviews" && (
          <div className="space-y-4">
            {reviews.map((review) => (
              <div key={review.id} className="bg-white rounded-lg shadow p-6">
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <h3 className="font-semibold text-lg">
                      {review.productName}
                    </h3>
                    <div className="flex items-center gap-1 mb-2">
                      {[1, 2, 3, 4, 5].map((star) => (
                        <span
                          key={star}
                          className={
                            star <= review.rating
                              ? "text-yellow-500"
                              : "text-gray-300"
                          }
                        >
                          ★
                        </span>
                      ))}
                    </div>
                  </div>
                  <button
                    onClick={() => deleteReview(review.id)}
                    className="text-red-600 hover:underline"
                  >
                    Delete
                  </button>
                </div>
                <p className="text-gray-700">{review.comment}</p>
                <p className="text-sm text-gray-500 mt-2">
                  {new Date(review.createdAt).toLocaleDateString()}
                </p>
              </div>
            ))}
            {reviews.length === 0 && (
              <p className="text-center py-8 text-gray-500">
                No reviews yet. Buy products and share your experience!
              </p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
