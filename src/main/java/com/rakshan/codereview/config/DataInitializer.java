package com.rakshan.codereview.config;

import com.rakshan.codereview.model.*;
import com.rakshan.codereview.model.enums.*;
import com.rakshan.codereview.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds the database with demo data on application startup.
 * Only runs if no users exist yet (fresh database).
 *
 * Demo credentials:
 *   - admin / admin123
 *   - alice / alice123
 *   - bob   / bob123
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CodeSubmissionRepository submissionRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewDimensionRepository dimensionRepository;
    private final ReviewRatingRepository ratingRepository;
    private final ReputationScoreRepository reputationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CodeSubmissionRepository submissionRepository,
                           ReviewRepository reviewRepository,
                           ReviewDimensionRepository dimensionRepository,
                           ReviewRatingRepository ratingRepository,
                           ReputationScoreRepository reputationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.reviewRepository = reviewRepository;
        this.dimensionRepository = dimensionRepository;
        this.ratingRepository = ratingRepository;
        this.reputationRepository = reputationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already has data — skipping demo seed.");
            return;
        }

        log.info("Seeding demo data...");

        // ── Users ────────────────────────────────────────────────────────
        User admin = userRepository.save(User.builder()
                .username("admin")
                .email("admin@codereview.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build());

        User alice = userRepository.save(User.builder()
                .username("alice")
                .email("alice@codereview.com")
                .password(passwordEncoder.encode("alice123"))
                .role(Role.USER)
                .build());

        User bob = userRepository.save(User.builder()
                .username("bob")
                .email("bob@codereview.com")
                .password(passwordEncoder.encode("bob123"))
                .role(Role.USER)
                .build());

        // ── Reputation Scores ────────────────────────────────────────────
        reputationRepository.save(ReputationScore.builder()
                .user(admin).totalScore(150.0).reviewCount(12).averageAccuracy(4.5).build());
        reputationRepository.save(ReputationScore.builder()
                .user(alice).totalScore(95.0).reviewCount(8).averageAccuracy(3.8).build());
        reputationRepository.save(ReputationScore.builder()
                .user(bob).totalScore(60.0).reviewCount(5).averageAccuracy(3.2).build());

        // ── Review Dimensions ────────────────────────────────────────────
        ReviewDimension readability = dimensionRepository.save(ReviewDimension.builder()
                .name("Readability")
                .description("How easy is the code to read and understand")
                .maxScore(5)
                .build());

        ReviewDimension performance = dimensionRepository.save(ReviewDimension.builder()
                .name("Performance")
                .description("Efficiency of algorithms and resource usage")
                .maxScore(5)
                .build());

        ReviewDimension security = dimensionRepository.save(ReviewDimension.builder()
                .name("Security")
                .description("Adherence to secure coding practices")
                .maxScore(5)
                .build());

        ReviewDimension maintainability = dimensionRepository.save(ReviewDimension.builder()
                .name("Maintainability")
                .description("How easy is the code to modify and extend")
                .maxScore(5)
                .build());

        // ── Code Submissions ─────────────────────────────────────────────

        // Alice's submissions
        CodeSubmission sub1 = submissionRepository.save(CodeSubmission.builder()
                .title("Binary Search in Java")
                .description("Implementation of binary search algorithm with edge case handling")
                .code("public class BinarySearch {\n"
                        + "    public static int search(int[] arr, int target) {\n"
                        + "        int left = 0, right = arr.length - 1;\n"
                        + "        while (left <= right) {\n"
                        + "            int mid = left + (right - left) / 2;\n"
                        + "            if (arr[mid] == target) return mid;\n"
                        + "            if (arr[mid] < target) left = mid + 1;\n"
                        + "            else right = mid - 1;\n"
                        + "        }\n"
                        + "        return -1;\n"
                        + "    }\n"
                        + "}")
                .language(Language.JAVA)
                .status(SubmissionStatus.REVIEWED)
                .user(alice)
                .assignedReviewer(bob)
                .build());

        CodeSubmission sub2 = submissionRepository.save(CodeSubmission.builder()
                .title("Python Flask REST API")
                .description("A simple REST API built with Flask and SQLAlchemy")
                .code("from flask import Flask, jsonify, request\n"
                        + "from flask_sqlalchemy import SQLAlchemy\n\n"
                        + "app = Flask(__name__)\n"
                        + "app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///app.db'\n"
                        + "db = SQLAlchemy(app)\n\n"
                        + "class Item(db.Model):\n"
                        + "    id = db.Column(db.Integer, primary_key=True)\n"
                        + "    name = db.Column(db.String(80), nullable=False)\n\n"
                        + "@app.route('/items', methods=['GET'])\n"
                        + "def get_items():\n"
                        + "    items = Item.query.all()\n"
                        + "    return jsonify([{'id': i.id, 'name': i.name} for i in items])\n\n"
                        + "@app.route('/items', methods=['POST'])\n"
                        + "def create_item():\n"
                        + "    data = request.get_json()\n"
                        + "    item = Item(name=data['name'])\n"
                        + "    db.session.add(item)\n"
                        + "    db.session.commit()\n"
                        + "    return jsonify({'id': item.id, 'name': item.name}), 201\n")
                .language(Language.PYTHON)
                .status(SubmissionStatus.UNDER_REVIEW)
                .user(alice)
                .assignedReviewer(admin)
                .build());

        // Bob's submissions
        CodeSubmission sub3 = submissionRepository.save(CodeSubmission.builder()
                .title("React useDebounce Hook")
                .description("Custom React hook for debouncing values with cleanup")
                .code("import { useState, useEffect } from 'react';\n\n"
                        + "export function useDebounce(value, delay = 300) {\n"
                        + "  const [debouncedValue, setDebouncedValue] = useState(value);\n\n"
                        + "  useEffect(() => {\n"
                        + "    const timer = setTimeout(() => {\n"
                        + "      setDebouncedValue(value);\n"
                        + "    }, delay);\n\n"
                        + "    return () => clearTimeout(timer);\n"
                        + "  }, [value, delay]);\n\n"
                        + "  return debouncedValue;\n"
                        + "}\n")
                .language(Language.JAVASCRIPT)
                .status(SubmissionStatus.REVIEWED)
                .user(bob)
                .assignedReviewer(alice)
                .build());

        CodeSubmission sub4 = submissionRepository.save(CodeSubmission.builder()
                .title("Go HTTP Server with Middleware")
                .description("Basic Go HTTP server demonstrating middleware pattern")
                .code("package main\n\n"
                        + "import (\n"
                        + "    \"fmt\"\n"
                        + "    \"log\"\n"
                        + "    \"net/http\"\n"
                        + "    \"time\"\n"
                        + ")\n\n"
                        + "func loggingMiddleware(next http.Handler) http.Handler {\n"
                        + "    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {\n"
                        + "        start := time.Now()\n"
                        + "        next.ServeHTTP(w, r)\n"
                        + "        log.Printf(\"%s %s %v\", r.Method, r.URL.Path, time.Since(start))\n"
                        + "    })\n"
                        + "}\n\n"
                        + "func helloHandler(w http.ResponseWriter, r *http.Request) {\n"
                        + "    fmt.Fprintln(w, \"Hello, World!\")\n"
                        + "}\n\n"
                        + "func main() {\n"
                        + "    mux := http.NewServeMux()\n"
                        + "    mux.HandleFunc(\"/\", helloHandler)\n"
                        + "    log.Fatal(http.ListenAndServe(\":8081\", loggingMiddleware(mux)))\n"
                        + "}\n")
                .language(Language.GO)
                .status(SubmissionStatus.PENDING_REVIEW)
                .user(bob)
                .build());

        // Admin's submissions
        CodeSubmission sub5 = submissionRepository.save(CodeSubmission.builder()
                .title("Rust Linked List")
                .description("Safe linked list implementation in Rust using Box pointers")
                .code("pub struct List<T> {\n"
                        + "    head: Option<Box<Node<T>>>,\n"
                        + "}\n\n"
                        + "struct Node<T> {\n"
                        + "    value: T,\n"
                        + "    next: Option<Box<Node<T>>>,\n"
                        + "}\n\n"
                        + "impl<T> List<T> {\n"
                        + "    pub fn new() -> Self {\n"
                        + "        List { head: None }\n"
                        + "    }\n\n"
                        + "    pub fn push(&mut self, value: T) {\n"
                        + "        let new_node = Box::new(Node {\n"
                        + "            value,\n"
                        + "            next: self.head.take(),\n"
                        + "        });\n"
                        + "        self.head = Some(new_node);\n"
                        + "    }\n\n"
                        + "    pub fn pop(&mut self) -> Option<T> {\n"
                        + "        self.head.take().map(|node| {\n"
                        + "            self.head = node.next;\n"
                        + "            node.value\n"
                        + "        })\n"
                        + "    }\n"
                        + "}\n")
                .language(Language.RUST)
                .status(SubmissionStatus.PENDING_REVIEW)
                .user(admin)
                .build());

        CodeSubmission sub6 = submissionRepository.save(CodeSubmission.builder()
                .title("TypeScript Generic Repository Pattern")
                .description("Generic repository pattern with TypeScript for data access layer")
                .code("interface Entity {\n"
                        + "  id: string;\n"
                        + "}\n\n"
                        + "interface Repository<T extends Entity> {\n"
                        + "  findById(id: string): Promise<T | null>;\n"
                        + "  findAll(): Promise<T[]>;\n"
                        + "  create(entity: Omit<T, 'id'>): Promise<T>;\n"
                        + "  update(id: string, entity: Partial<T>): Promise<T>;\n"
                        + "  delete(id: string): Promise<void>;\n"
                        + "}\n\n"
                        + "class InMemoryRepository<T extends Entity> implements Repository<T> {\n"
                        + "  private items: Map<string, T> = new Map();\n\n"
                        + "  async findById(id: string): Promise<T | null> {\n"
                        + "    return this.items.get(id) ?? null;\n"
                        + "  }\n\n"
                        + "  async findAll(): Promise<T[]> {\n"
                        + "    return Array.from(this.items.values());\n"
                        + "  }\n\n"
                        + "  async create(entity: Omit<T, 'id'>): Promise<T> {\n"
                        + "    const id = crypto.randomUUID();\n"
                        + "    const item = { ...entity, id } as T;\n"
                        + "    this.items.set(id, item);\n"
                        + "    return item;\n"
                        + "  }\n\n"
                        + "  async update(id: string, entity: Partial<T>): Promise<T> {\n"
                        + "    const existing = this.items.get(id);\n"
                        + "    if (!existing) throw new Error('Not found');\n"
                        + "    const updated = { ...existing, ...entity };\n"
                        + "    this.items.set(id, updated);\n"
                        + "    return updated;\n"
                        + "  }\n\n"
                        + "  async delete(id: string): Promise<void> {\n"
                        + "    this.items.delete(id);\n"
                        + "  }\n"
                        + "}\n")
                .language(Language.TYPESCRIPT)
                .status(SubmissionStatus.REVIEWED)
                .user(admin)
                .assignedReviewer(alice)
                .build());

        // ── Reviews with Ratings ─────────────────────────────────────────

        // Bob reviewed Alice's Binary Search (sub1)
        Review review1 = Review.builder()
                .codeSubmission(sub1)
                .reviewer(bob)
                .comments("Clean implementation of binary search. Good use of the overflow-safe midpoint calculation (left + (right - left) / 2). Consider adding null check for the input array and handling empty arrays explicitly.")
                .status(ReviewStatus.SUBMITTED)
                .build();
        review1 = reviewRepository.save(review1);

        ratingRepository.save(ReviewRating.builder()
                .review(review1).dimension(readability).score(5).build());
        ratingRepository.save(ReviewRating.builder()
                .review(review1).dimension(performance).score(4).build());
        ratingRepository.save(ReviewRating.builder()
                .review(review1).dimension(security).score(3).build());
        ratingRepository.save(ReviewRating.builder()
                .review(review1).dimension(maintainability).score(4).build());

        // Alice reviewed Bob's React Hook (sub3)
        Review review2 = Review.builder()
                .codeSubmission(sub3)
                .reviewer(alice)
                .comments("Nice custom hook! The cleanup function in useEffect is correctly implemented. One suggestion: consider adding a TypeScript generic type parameter so the hook works with any value type, not just strings.")
                .status(ReviewStatus.SUBMITTED)
                .build();
        review2 = reviewRepository.save(review2);

        ratingRepository.save(ReviewRating.builder()
                .review(review2).dimension(readability).score(5).build());
        ratingRepository.save(ReviewRating.builder()
                .review(review2).dimension(performance).score(4).build());
        ratingRepository.save(ReviewRating.builder()
                .review(review2).dimension(security).score(4).build());
        ratingRepository.save(ReviewRating.builder()
                .review(review2).dimension(maintainability).score(5).build());

        // Alice reviewed Admin's TypeScript Repository (sub6)
        Review review3 = Review.builder()
                .codeSubmission(sub6)
                .reviewer(alice)
                .comments("Excellent use of generics and the Repository pattern. The InMemoryRepository is a great starting point. For production, consider adding pagination to findAll() and error handling for the update method when partial updates conflict with required fields.")
                .status(ReviewStatus.SUBMITTED)
                .build();
        review3 = reviewRepository.save(review3);

        ratingRepository.save(ReviewRating.builder()
                .review(review3).dimension(readability).score(4).build());
        ratingRepository.save(ReviewRating.builder()
                .review(review3).dimension(performance).score(3).build());
        ratingRepository.save(ReviewRating.builder()
                .review(review3).dimension(security).score(4).build());
        ratingRepository.save(ReviewRating.builder()
                .review(review3).dimension(maintainability).score(5).build());

        log.info("Demo data seeded successfully!");
        log.info("  Users: admin/admin123, alice/alice123, bob/bob123");
        log.info("  Submissions: 6, Reviews: 3, Dimensions: 4");
    }
}
