import { Outlet } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";

export default function ClientLayout() {
  return (
    <div>
      <Header />
      <main className="client-main">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
